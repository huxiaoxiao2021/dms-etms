package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.spotcheck.domain.DwsAIDistinguishNotifyMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * DWS抽检设备图片AI识别消费
 *
 * @author hujiping
 * @date 2022/3/17 4:09 PM
 */
@Service("dwsAIDistinguishNotifyConsumer")
public class DwsAIDistinguishNotifyConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DwsAIDistinguishNotifyConsumer.class);

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("SpotCheckDeviceAIDistinguishConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("设备图片AI识别回传消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            DwsAIDistinguishNotifyMQ distinguishNotifyMQ = JsonHelper.fromJsonUseGson(message.getText(), DwsAIDistinguishNotifyMQ.class);
            if(distinguishNotifyMQ == null) {
                logger.warn("设备图片AI识别消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            String waybillCode = distinguishNotifyMQ.getWaybillCode();
            Integer siteCode = distinguishNotifyMQ.getSiteCode();
            // 图片不合规：记录异常单号及原因
            if(!Objects.equals(distinguishNotifyMQ.getStatus(), String.valueOf(Constants.SUCCESS_CODE))){
                WeightVolumeSpotCheckDto dto = new WeightVolumeSpotCheckDto();
                dto.setPackageCode(waybillCode);
                dto.setReviewSiteCode(siteCode);
                dto.setSpotCheckStatus(SpotCheckStatusEnum.SPOT_CHECK_STATUS_INVALID_AI_FAIL.getCode());
                String distinguishFailMessage = (StringUtils.isEmpty(distinguishNotifyMQ.getPackageCode()) ? Constants.EMPTY_FILL : distinguishNotifyMQ.getPackageCode())
                        + Constants.SEPARATOR_HYPHEN + distinguishNotifyMQ.getMessage();
                dto.setPictureAIDistinguishReason(distinguishFailMessage);
                spotCheckServiceProxy.insertOrUpdateProxyReform(dto);
                return;
            }
            // 图片合规：下发数据
            SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
            condition.setWaybillCode(waybillCode);
            condition.setReviewSiteCode(siteCode);
            condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            condition.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
            List<WeightVolumeSpotCheckDto> spotCheckDtoList = spotCheckQueryManager.querySpotCheckByCondition(condition);
            if(CollectionUtils.isEmpty(spotCheckDtoList) || spotCheckDtoList.get(0) == null){
                logger.warn("根据运单号:{}场地:{}未查询到抽检数据!", waybillCode, siteCode);
                return;
            }
            spotCheckDealService.executeIssue(spotCheckDtoList.get(0));
        } catch (SpotCheckSysException e) {
            logger.warn("设备图片AI识别MQ处理异常进行重试", e);
            throw e;
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("处理设备图片AI识别失败, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }
}
