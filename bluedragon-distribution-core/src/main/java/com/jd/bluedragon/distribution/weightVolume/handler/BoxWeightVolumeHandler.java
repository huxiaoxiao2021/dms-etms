package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BoxOperateApiManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeContext;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeDto;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetBoxWeightVolumeMq;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetErrorRes;
import com.jd.bluedragon.external.crossbow.economicNet.domain.EconomicNetResult;
import com.jd.bluedragon.external.crossbow.economicNet.manager.EconomicNetBusinessManager;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.alibaba.fastjson.JSONObject;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.zhongyouex.order.api.dto.BoxDetailInfoDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    private BoxOperateApiManager boxOperateApiManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    protected void weightVolumeRuleCheckHandler(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(weightVolumeContext.getVolume() <= Constants.DOUBLE_ZERO){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_7);
        }
        // 箱号称重默认使用C网称重校验逻辑
        checkCInternetRule(weightVolumeContext, result);
    }

    private void boxWeightBasicCheck(WeightVolumeRuleCheckDto condition, InvokeResult<Boolean> result) {
        if(condition.getVolume() <= Constants.DOUBLE_ZERO
                && condition.getHeight() * condition.getHeight() * condition.getHeight() <= Constants.DOUBLE_ZERO){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_7);
        }
    }

    @Override
    protected void basicVerification(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(!BusinessHelper.isBoxcode(weightVolumeContext.getBarCode())){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_0);
            return;
        }
        if(Objects.equals(weightVolumeContext.getCheckWeight(),true)){
            if(weightVolumeContext.getWeight() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_1);
                return;
            }
        }
        if(Objects.equals(weightVolumeContext.getCheckLWH(),true)){
            if(weightVolumeContext.getLength() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_2);
                return;
            }
            if(weightVolumeContext.getWidth() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_3);
                return;
            }
            if(weightVolumeContext.getHeight() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_4);
                return;
            }
        }
        if(Objects.equals(weightVolumeContext.getCheckVolume(),true)){
            if(weightVolumeContext.getVolume() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_5);
            }
        }
    }

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setBoxCode(entity.getBarCode());
        if (entity.getWidth() != null && entity.getLength() != null && entity.getHeight() != null && !NumberHelper.gt0(entity.getVolume())) {
            entity.setVolume(entity.getWidth() * entity.getLength() * entity.getHeight());
        }
        //修复兼容 只传入总体积时拆分长宽高 体积立方厘米 长宽高厘米
        if (entity.getWidth() == null && entity.getLength() == null && entity.getHeight() == null && NumberHelper.gt0(entity.getVolume())) {
            entity.setLength(1.0);
            entity.setWidth(1.0);
            entity.setHeight(entity.getVolume());
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
        /*兼容上线逻辑，从经济网接口继续获取箱包关系  取合集*/
        //获取装箱明细
        List<BoxDetailInfoDto> resultDto = boxOperateApiManager.findBoxDetailInfoList(entity.getBoxCode());
        if(!CollectionUtils.isEmpty(resultDto)){
            for(BoxDetailInfoDto boxDetailInfoDto : resultDto){
                if(boxDetailInfoDto != null && StringUtils.isNotBlank(boxDetailInfoDto.getWaybillCode())){
                    waybillList.add(boxDetailInfoDto.getWaybillCode());
                }
            }
        }else{
            logger.error("BoxWeightVolumeHandler 未从经济网接口中获取箱号数据{}   返回值 {}",entity.getBoxCode(),JsonHelper.toJson(resultDto));
        }
        if(CollectionUtils.isEmpty(waybillList)){
            logger.error("BoxWeightVolumeHandler waybillList is empty {} ",entity.getBoxCode());
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

//            long startTime = System.currentTimeMillis();

//            经济网：众邮按箱称重业务由原来的调用外部接口变更为发送MQ：economic_net_box_weight， 联系人：kongchunfei，huangchang9，变更时间：2022-10-13 20:45:14，拣运侧产品：zhangshuo37知晓
//            try {
//                EconomicNetResult<EconomicNetErrorRes> result = economicNetBusinessManager.doRestInterface(weightVolumeDto);
//                logger.info("推送箱号信息，经济网返回{}", JsonHelper.toJson(result));
//                retryOnFailDoRestInterface(result, weightVolumeDto, entity, startTime, null);
//            } catch (Exception e) {
//                retryOnFailDoRestInterface(null, weightVolumeDto, entity, startTime, e);
//            }

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

        if (e != null) {
            response.put("msg", e.getMessage());
            response.put("code", "500");
        } else if (result == null) {
            response.put("msg", "NULL");
            response.put("code", "500");
        } else if (!"0000".equals(result.getCode())) {
            response.put("msg", result.getMsg());
            response.put("code", result.getCode());
        } else {
            response.put("msg", "成功");
            response.put("code", "200");
        }

        BusinessLogProfiler logProfiler = new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.ECONOMIC_NET_BOX_WEIGHT)
                .processTime(System.currentTimeMillis(), startTime)
                .operateRequest(request)
                .operateResponse(response)
                .methodName("EconomicNetBusinessManager#doRestInterface")
                .build();
        logEngine.addLog(logProfiler);
        logger.warn("推送箱号信息至经济网异常:{}", JsonHelper.toJson(response));
        // 出现异常、无响应结果、响应结果非成功code码 则 抛出异常 自动重试
        if (e != null || result == null || !"0000".equals(result.getCode())) {
            if(uccPropertyConfiguration.getEconomicNetPushZTDRetry()){
                throw new RuntimeException(MessageFormat.format(entity.getBoxCode()+"推送箱号信息至经济网失败,异常原因：{0}", JsonHelper.toJson(response)));
            }
        }
    }
}
