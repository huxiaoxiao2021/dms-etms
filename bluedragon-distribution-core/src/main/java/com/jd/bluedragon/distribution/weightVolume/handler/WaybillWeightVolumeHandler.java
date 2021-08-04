package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeContext;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.exception.JMQException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *     发送MQ:dms_waybill_weight
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("waybillWeightVolumeHandler")
public class WaybillWeightVolumeHandler extends AbstractWeightVolumeHandler {

    /* MQ消息生产者： topic:dms_waybill_weight*/
    @Autowired
    @Qualifier("weighByWaybillProducer")
    private DefaultJMQProducer weighByWaybillProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DmsWeightFlowService dmsWeightFlowService;

    @Override
    protected void weightVolumeRuleCheckHandler(WeightVolumeRuleCheckDto condition, WeightVolumeRuleConstant weightVolumeRuleConstant,
                                                Waybill waybill,InvokeResult<Boolean> result) {
        if(BusinessUtil.isCInternet(waybill.getWaybillSign())){
            checkCInternetRule(condition,weightVolumeRuleConstant,result);
            return;
        }
        checkBInternetRule(condition,weightVolumeRuleConstant,waybill,result);
    }

    @Override
    protected void basicVerification(WeightVolumeRuleCheckDto condition, WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(!WaybillUtil.isWaybillCode(condition.getBarCode()) && !WaybillUtil.isPackageCode(condition.getBarCode())){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_0);
            return;
        }
        // 信任商家不校验重量体积（站点平台打印除外）
        Waybill waybill = weightVolumeContext.getWaybill();
        if(!Objects.equals(condition.getSourceCode(), FromSourceEnum.DMS_CLIENT_SITE_PLATE_PRINT.name())){
            if(BusinessHelper.isTrust(waybill.getWaybillSign())){
                return;
            }
        }
        if(Objects.equals(condition.getCheckWeight(),true)){
            if(condition.getWeight() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_1);
                return;
            }
        }
        if(Objects.equals(condition.getCheckVolume(),true)){
            if(condition.getVolume() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_5);
            }
        }
    }


    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));

        WaybillWeightDTO weightDTO = new WaybillWeightDTO();
        weightDTO.setWaybillCode(entity.getWaybillCode());
        weightDTO.setOperatorSiteCode(entity.getOperateSiteCode());
        weightDTO.setOperatorSiteName(entity.getOperateSiteName());
        weightDTO.setOperatorId(entity.getOperatorId());
        weightDTO.setOperatorName(entity.getOperatorName());
        weightDTO.setWeight(entity.getWeight());
        if (NumberHelper.gt0(entity.getVolume())) {
            weightDTO.setVolume(entity.getVolume());
        } else if (entity.getLength() != null && entity.getWidth() != null && entity.getHeight() != null) {
            weightDTO.setVolume(entity.getHeight() * entity.getWidth() * entity.getLength());
        }
        weightDTO.setOperateTimeMillis(entity.getOperateTime().getTime());
        try {
            //todo 梳理是否还有运单不存在的情况
            BaseEntity<Waybill> baseWaybillEntity = waybillQueryManager.getWaybillByWaybillCode(entity.getWaybillCode());
            if (baseWaybillEntity != null && null != baseWaybillEntity.getData()) {
                weightDTO.setStatus(10);
            } else {
                weightDTO.setStatus(20);
            }
            weighByWaybillProducer.send(entity.getWaybillCode(), JsonHelper.toJson(weightDTO));

            /* 记录原始的称重流水 */
            DmsWeightFlow dmsWeightFlow = new DmsWeightFlow();
            dmsWeightFlow.setBusinessType(Constants.BUSINESS_TYPE_WEIGHT);
            dmsWeightFlow.setOperateType(Constants.OPERATE_TYPE_WEIGHT_BY_WAYBILL);
            dmsWeightFlow.setDmsSiteCode(entity.getOperateSiteCode());
            dmsWeightFlow.setDmsSiteName(entity.getOperateSiteName());
            dmsWeightFlow.setWaybillCode(entity.getWaybillCode());
            dmsWeightFlow.setWeight(entity.getWeight());
            dmsWeightFlow.setVolume(entity.getVolume());
            dmsWeightFlow.setOperateTime(entity.getOperateTime());
            dmsWeightFlow.setOperatorCode(entity.getOperatorId());
            dmsWeightFlow.setOperatorName(entity.getOperatorName());
            boolean bool = dmsWeightFlowService.saveOrUpdate(dmsWeightFlow);
            if (!bool) {
                logger.error("记录称重流水库失败：{}",JsonHelper.toJson(entity));
            }

        } catch (JMQException e) {
            logger.error("发送MQ-TOPIC【{}】消息失败，消息体为：{}",weighByWaybillProducer.getTopic(),JsonHelper.toJson(weightDTO));
        }
    }

}
