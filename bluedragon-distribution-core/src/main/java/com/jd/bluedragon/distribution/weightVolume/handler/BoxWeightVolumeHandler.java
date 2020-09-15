package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeDto;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeMq;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetErrorRes;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetResult;
import com.jd.bluedragon.external.crossbow.economicNet.manager.EconomicNetBusinessManager;
import com.jd.bluedragon.utils.BaseContants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.fastjson.JSONObject;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jd.bluedragon.Constants.TENANT_CODE_ECONOMIC;

/**
 * <p>
 *     按箱称重的处理类
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("boxWeightVolumeHandler")
public class BoxWeightVolumeHandler extends AbstractWeightVolumeHandler {

    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Autowired
    private SiteService siteService;

    @Autowired
    private EconomicNetBusinessManager economicNetBusinessManager;

    @Autowired
    @Qualifier("economicNetBoxWeightProducer")
    private DefaultJMQProducer economicNetBoxWeightProducer;

    @Autowired
    private LogEngine logEngine;

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setBoxCode(entity.getBarCode());
        if (entity.getWidth() != null && entity.getLength() != null && entity.getHeight() != null && !NumberHelper.gt0(entity.getVolume())) {
            entity.setVolume(entity.getWidth() * entity.getLength() * entity.getHeight());
        }

        /* 获取箱号的信息 */
        Box box = boxService.findBoxByCode(entity.getBoxCode());
        if (box == null) {
            logger.error("根据容器号{}获取容器信息失败",entity.getBoxCode());
            return;
        }

        /* 存储箱号的运单信息，保留不重复项 */
        Set<String> waybillList = new HashSet<>();

        /* 从始发交接明细中获取箱号的明细 */
        List<ThirdBoxDetail> thirdBoxDetails = thirdBoxDetailService.queryByBoxCode(TENANT_CODE_ECONOMIC,box.getCreateSiteCode(),entity.getBoxCode());
        if (thirdBoxDetails != null && !thirdBoxDetails.isEmpty()) {
            for (ThirdBoxDetail thirdBoxDetail : thirdBoxDetails) {
                String waybillCode = WaybillUtil.isWaybillCode(thirdBoxDetail.getWaybillCode())?
                        thirdBoxDetail.getWaybillCode() : WaybillUtil.getWaybillCode(thirdBoxDetail.getPackageCode());
                if (waybillList.contains(waybillCode)) {
                    logger.warn("三方装箱检测到重复的运单装箱数据：{}，{}", entity.getBoxCode(), waybillCode);
                    continue;
                }
                /* 如果SET集合中不包含该运动号则添加 */
                waybillList.add(waybillCode);
            }
        }

        if (waybillList.isEmpty()) {
            logger.warn("获取此箱号的装箱明细数据为空：{}",entity.getBarCode());
            return;
        }

        Double itemWeight = entity.getWeight() == null? null :  entity.getWeight() / waybillList.size();
        Double itemLength = entity.getLength();
        Double itemWidth = entity.getWidth();
        Double itemHeight = entity.getHeight() == null? null : entity.getHeight() / waybillList.size();
        Double itemVolume = entity.getVolume() == null? null : entity.getVolume() / waybillList.size();
        /* 循环处理箱明细 */
        for (String waybillCode : waybillList) {
            WeightVolumeEntity itemEntity = new WeightVolumeEntity();
            itemEntity.setBarCode(waybillCode);
            itemEntity.setWaybillCode(waybillCode);
            itemEntity.setBoxCode(entity.getBoxCode());
            itemEntity.setVolume(itemVolume);
            itemEntity.setWeight(itemWeight);
            itemEntity.setLength(itemLength);
            itemEntity.setWidth(itemWidth);
            itemEntity.setHeight(itemHeight);
            itemEntity.setOperateSiteCode(entity.getOperateSiteCode());
            itemEntity.setOperateSiteName(entity.getOperateSiteName());
            itemEntity.setOperatorId(entity.getOperatorId());
            itemEntity.setOperatorCode(entity.getOperatorCode());
            itemEntity.setOperatorName(entity.getOperatorName());
            itemEntity.setOperateTime(entity.getOperateTime());
            itemEntity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL);
            itemEntity.setSourceCode(FromSourceEnum.DMS_INNER_SPLIT);
            /* 这个地方的handoverFlag设置为false，可以放到entity对象中，传过来 */
            weightVolumeHandlerStrategy.doHandler(itemEntity);
        }

        /* 经济网的箱号需要回传信息给经济网:经济网箱号的判断条件--始发是经济网 */
        BaseStaffSiteOrgDto siteEntity = siteService.getSite(box.getCreateSiteCode());
        if (siteEntity != null && siteEntity.getSiteType() == BaseContants.ECONOMIC_NET_SITE) {
            EconomicNetBoxWeightVolumeDto weightVolumeDto = new EconomicNetBoxWeightVolumeDto();
            weightVolumeDto.setId(String.valueOf(System.currentTimeMillis()));
            weightVolumeDto.setBagCode(entity.getBoxCode());
            weightVolumeDto.setHeight(String.valueOf(entity.getHeight()));
            weightVolumeDto.setLength(String.valueOf(entity.getLength()));
            weightVolumeDto.setWeight(String.valueOf(entity.getWeight()));
            weightVolumeDto.setWidth(String.valueOf(entity.getWidth()));
            weightVolumeDto.setScanDate(DateHelper.formatDateTime(entity.getOperateTime()));
            weightVolumeDto.setScanMan(entity.getOperatorName());
            weightVolumeDto.setScanSite(entity.getOperateSiteName());
            weightVolumeDto.setScanSiteCode(String.valueOf(entity.getOperateSiteCode()));
            weightVolumeDto.setScanType("包裹称重扫描");

            long startTime = System.currentTimeMillis();
            try {
                EconomicNetResult<EconomicNetErrorRes> result = economicNetBusinessManager.doRestInterface(weightVolumeDto);
                logger.info("推送箱号信息，经济网返回{}", JsonHelper.toJson(result));
                retryOnFailDoRestInterface(result, weightVolumeDto, entity, startTime, null);
            } catch (Exception e) {
                retryOnFailDoRestInterface(null, weightVolumeDto, entity, startTime, e);
            }

            EconomicNetBoxWeightVolumeMq economicNetBoxWeightVolumeMq = new EconomicNetBoxWeightVolumeMq();
            BeanUtils.copyProperties(weightVolumeDto,economicNetBoxWeightVolumeMq);
            economicNetBoxWeightVolumeMq.setScanManCode(entity.getOperatorCode());
            economicNetBoxWeightVolumeMq.setScanManId(entity.getOperatorId());
            if(logger.isInfoEnabled()){
                logger.info("众邮箱号称重发送MQ【{}】,业务ID【{}】,消息体【{}】",
                        economicNetBoxWeightProducer.getTopic(),economicNetBoxWeightVolumeMq.getBagCode(),JsonHelper.toJson(economicNetBoxWeightVolumeMq));
            }
            economicNetBoxWeightProducer.sendOnFailPersistent(economicNetBoxWeightVolumeMq.getBagCode(),JsonHelper.toJson(economicNetBoxWeightVolumeMq));
        }

    }

    /**
     * 推送经济网箱号信息失败重试
     * 抛出异常 框架会自动重试
     *
     * @param result      doRestInterface方法 响应结果
     * @param requestBody doRestInterface方法 请求参数
     * @param startTime   doRestInterface方法 执行开始时间
     * @param e           异常
     */
    private void retryOnFailDoRestInterface(EconomicNetResult<EconomicNetErrorRes> result, EconomicNetBoxWeightVolumeDto requestBody, WeightVolumeEntity entity, long startTime, Exception e) {
        // 出现异常、无响应结果、响应结果非成功code码 则 写入 业务日志
        if (e != null || result == null || !"0000".equals(result.getCode())) {
            Object responseBody = (e == null ? result : e.getMessage());

            JSONObject request = new JSONObject();
            request.put("waybillCode", entity.getWaybillCode());
            request.put("packageCode", entity.getPackageCode());
            request.put("boxCode", entity.getBoxCode());

            request.put("operatorName", requestBody.getScanMan());
            request.put("siteCode", requestBody.getScanSiteCode());
            request.put("siteName", requestBody.getScanSite());
            request.put("operateTime", requestBody.getScanDate());

            JSONObject response = new JSONObject();
            response.put("bagCode", requestBody.getBagCode());
            response.put("scanSiteCode", requestBody.getScanSiteCode());
            response.put("content", JsonHelper.toJson(responseBody));

            BusinessLogProfiler logProfiler = new BusinessLogProfilerBuilder()
                    .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.ECONOMIC_NET_BOX_WEIGHT)
                    .processTime(System.currentTimeMillis(), startTime)
                    .operateRequest(request)
                    .operateResponse(response)
                    .methodName("EconomicNetBusinessManager#doRestInterface")
                    .build();
            logEngine.addLog(logProfiler);
            logger.warn("推送箱号信息至经济网异常:{}", JsonHelper.toJson(response));
            throw new RuntimeException(MessageFormat.format("推送箱号信息至经济网失败,异常原因：{0}", JsonHelper.toJson(response)));
        }
    }
}
