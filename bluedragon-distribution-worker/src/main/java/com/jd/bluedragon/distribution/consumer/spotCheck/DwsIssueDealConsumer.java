package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.spotcheck.domain.DwsAIDistinguishMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * dws抽检图片下发AI消息处理消费
 *
 * @author hujiping
 * @date 2022/3/17 8:47 PM
 */
@Service("dwsIssueDealConsumer")
public class DwsIssueDealConsumer  extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DwsIssueDealConsumer.class);

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    @Qualifier("dwsAIDistinguishBigProducer")
    private DefaultJMQProducer dwsAIDistinguishBigProducer;

    @Autowired
    @Qualifier("dwsAIDistinguishSmallProducer")
    private DefaultJMQProducer dwsAIDistinguishSmallProducer;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DwsIssueDealConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("dws下发处理消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            WeightVolumeSpotCheckDto spotCheckDto = JsonHelper.fromJsonUseGson(message.getText(), WeightVolumeSpotCheckDto.class);
            if(spotCheckDto == null || StringUtils.isEmpty(spotCheckDto.getWaybillCode()) || spotCheckDto.getReviewSiteCode() == null) {
                logger.warn("dws下发处理消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            if(checkWaybillIsAiSend(spotCheckDto.getWaybillCode())){
                return;
            }
            // 一单多件：获取抽检的包裹数据
            SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
            condition.setWaybillCode(spotCheckDto.getWaybillCode());
            condition.setReviewSiteCode(spotCheckDto.getReviewSiteCode());
            condition.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
            List<WeightVolumeSpotCheckDto> spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
            if(CollectionUtils.isEmpty(spotCheckList)){
                logger.warn("根据包裹:{}站点:{}未查询到抽检数据!", spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode());
                return;
            }
            List<DwsAIDistinguishMQ.Package> list = new ArrayList<>();
            for (WeightVolumeSpotCheckDto detailSpotCheck : spotCheckList) {
                String packagePicUrl = StringUtils.isEmpty(detailSpotCheck.getPictureAddress())
                        ? spotCheckDealService.getSpotCheckPackUrlFromCache(spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode()) : detailSpotCheck.getPictureAddress();
                if(StringUtils.isEmpty(packagePicUrl)){
                    logger.warn("包裹:{}站点:{}的抽检图片尚未上传!", spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode());
                    return;
                }
                DwsAIDistinguishMQ.Package packageUrl = new DwsAIDistinguishMQ.Package();
                packageUrl.setPackageCode(detailSpotCheck.getPackageCode());
                packageUrl.setPicUrl(packagePicUrl);
                list.add(packageUrl);
            }
            DwsAIDistinguishMQ dwsAIDistinguishMQ = new DwsAIDistinguishMQ();
            dwsAIDistinguishMQ.setUuid(spotCheckDto.getWaybillCode().concat(Constants.SEPARATOR_HYPHEN).concat(String.valueOf(System.currentTimeMillis())));
            dwsAIDistinguishMQ.setWaybillCode(spotCheckDto.getWaybillCode());
            dwsAIDistinguishMQ.setSiteCode(spotCheckDto.getReviewSiteCode());
            dwsAIDistinguishMQ.setPackages(list);
            if(list.size() > uccPropertyConfiguration.getDeviceAIDistinguishPackNum()){
                dwsAIDistinguishBigProducer.sendOnFailPersistent(dwsAIDistinguishMQ.getWaybillCode(), JsonHelper.toJson(dwsAIDistinguishMQ));
            }else {
                dwsAIDistinguishSmallProducer.sendOnFailPersistent(dwsAIDistinguishMQ.getWaybillCode(), JsonHelper.toJson(dwsAIDistinguishMQ));
            }
            // 设置下发AI缓存
            setAiSendWaybillCache(spotCheckDto.getWaybillCode());
        } catch (SpotCheckSysException e) {
            logger.warn("dws下发处理消息体处理异常进行重试", e);
            throw e;
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("dws下发处理消息体处理失败, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 校验运单是否已下发AI
     *
     * @param waybillCode
     * @return
     */
    private boolean checkWaybillIsAiSend(String waybillCode) {
        try {
            String cacheAiSendWaybillKey = String.format(CacheKeyConstants.CACHE_AI_SEND_WAYBILL, waybillCode);
            return jimdbCacheService.exists(cacheAiSendWaybillKey);
        }catch (Exception e){
            logger.error("校验运单号:{}是否下发AI异常!", waybillCode);
        }
        return false;
    }

    private void setAiSendWaybillCache(String waybillCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_AI_SEND_WAYBILL, waybillCode);
            jimdbCacheService.setEx(key, Constants.CONSTANT_NUMBER_ONE, 30, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置运单号:{}下发AI的缓存异常!", waybillCode, e);
        }
    }
}
