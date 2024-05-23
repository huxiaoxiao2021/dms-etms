package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightDTO;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightNoTraceDTO;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeContext;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Qualifier("waybillWeightFlowNoTraceProducer")
    private DefaultJMQProducer waybillWeightFlowNoTraceProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DmsWeightFlowService dmsWeightFlowService;

    @Override
    protected void weightVolumeRuleCheckHandler(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(commonCheckIntercept(weightVolumeContext, result)){
            return;
        }
        if(BusinessUtil.isCInternet(weightVolumeContext.getWaybill().getWaybillSign())){
            checkCInternetRule(weightVolumeContext, result);
            return;
        }
        checkBInternetRule(weightVolumeContext, result);
    }

    @Override
    protected void basicVerification(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(!WaybillUtil.isWaybillCode(weightVolumeContext.getBarCode()) && !WaybillUtil.isPackageCode(weightVolumeContext.getBarCode())){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_0);
            return;
        }
        if(Objects.equals(weightVolumeContext.getCheckWeight(),true)){
            if(weightVolumeContext.getWeight() <= Constants.DOUBLE_ZERO){
                result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_1);
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

        entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        
        //上传-超长超重服务信息
    	uploadOverWeightInfo(entity);
        
        // 上传运单称重数据
        uploadWaybillWeightInfo(entity);

        // 记录称重操作流水
        jyOperateFlowService.sendWeightVolumeOperateFlowData(entity, OperateBizSubTypeEnum.WAYBILL_WEIGHT_VOLUME);
        
        // 记录自己内部称重流水表
        recordOwnWeightTable(entity);
    }

    private void recordOwnWeightTable(WeightVolumeEntity entity) {
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
    }

    private void uploadWaybillWeightInfo(WeightVolumeEntity entity) {
        if(entity.getUploadWaybillFlowFlag()){
            if(entity.getRecordWaybillTraceFlag()){
                // 即上传运单称重流水也发全程跟踪
                unloadWaybillWeightFlowWithTrace(entity);
            }else {
                // 只上传运单称重流水不发全程跟踪
                unloadWaybillWeightFlowWithNoTrace(entity);
            }
        }
    }

    private void unloadWaybillWeightFlowWithNoTrace(WeightVolumeEntity entity) {
        WaybillWeightNoTraceDTO waybillWeightNoTraceDTO = WaybillWeightNoTraceDTO.builder()
                .waybillCode(entity.getWaybillCode())
                .weight(BigDecimal.valueOf(entity.getWeight()))
                // 因运单维度没有长宽高，故取 长=体积、宽=1、高=1 的方式设置（运单那边不想支持，所以只能沿用京牛的消息。。。。）
                .pLength(BigDecimal.valueOf(entity.getVolume())).pWidth(BigDecimal.valueOf(1)).pHigh(BigDecimal.valueOf(1))
                .operatorId(entity.getOperatorId()).operatorName(entity.getOperatorName())
                .opeSiteId(entity.getOperateSiteCode()).opeSiteName(entity.getOperateSiteName())
                .operateTimeMillis(entity.getOperateTime() == null ? System.currentTimeMillis() : entity.getOperateTime().getTime())
                .build();
        String waybillWeightNoTraceMsg = JsonHelper.toJson(waybillWeightNoTraceDTO);
        if(logger.isInfoEnabled()){
            logger.info("单号:{}上传称重流水不发全程跟踪，消息体:{}", entity.getWaybillCode(), waybillWeightNoTraceMsg);
        }
        waybillWeightFlowNoTraceProducer.sendOnFailPersistent(entity.getWaybillCode(), waybillWeightNoTraceMsg);
    }

    private void unloadWaybillWeightFlowWithTrace(WeightVolumeEntity entity) {
        WaybillWeightDTO weightDTO = new WaybillWeightDTO();
        weightDTO.setWaybillCode(entity.getWaybillCode());
        weightDTO.setOperatorSiteCode(entity.getOperateSiteCode());
        weightDTO.setOperatorSiteName(entity.getOperateSiteName());
        weightDTO.setOperatorId(entity.getOperatorId());
        weightDTO.setOperatorName(entity.getOperatorName());
        weightDTO.setWeight(entity.getWeight());
        weightDTO.setLongPackage(entity.getLongPackage());
        if (NumberHelper.gt0(entity.getVolume())) {
            weightDTO.setVolume(entity.getVolume());
        } else if (entity.getLength() != null && entity.getWidth() != null && entity.getHeight() != null) {
            weightDTO.setVolume(entity.getHeight() * entity.getWidth() * entity.getLength());
        }
        weightDTO.setOperateTimeMillis(entity.getOperateTime().getTime());
        BaseEntity<Waybill> baseWaybillEntity = waybillQueryManager.getWaybillByWaybillCode(entity.getWaybillCode());
        if (baseWaybillEntity != null && null != baseWaybillEntity.getData()) {
            weightDTO.setStatus(10);
        } else {
            weightDTO.setStatus(20);
        }
        weighByWaybillProducer.sendOnFailPersistent(entity.getWaybillCode(), JsonHelper.toJson(weightDTO));
    }

}
