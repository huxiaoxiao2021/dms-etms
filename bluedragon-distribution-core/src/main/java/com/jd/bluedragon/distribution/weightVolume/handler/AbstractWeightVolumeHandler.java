package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public abstract class AbstractWeightVolumeHandler implements IWeightVolumeHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${weight.volume.rule.config:}")
    private String weightVolumeRuleConfig;

    @Autowired
    protected WeightVolumeFlowJSFService weightVolumeFlowJSFService;

    @Autowired
    @Qualifier("dmsWeightVolumeFlowProducer")
    private DefaultJMQProducer dmsWeightVolumeFlowProducer;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public InvokeResult<Boolean> handlerOperateWeightVolume(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        result.setData(Boolean.TRUE);
        /* 处理称重量方任务 */
        handlerWeighVolume(entity);

        /* 处理之后记录流水 */
        postHandler(entity);

        return result;
    }

    protected abstract void handlerWeighVolume(WeightVolumeEntity entity);

    protected void postHandler(WeightVolumeEntity entity) {
        //推送统一称重量方消息
        dmsWeightVolumeFlowProducer.sendOnFailPersistent(entity.getBarCode(),JsonHelper.toJson(entity));

        WeightVolumeFlowEntity weightVolumeEntity = new WeightVolumeFlowEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        weightVolumeFlowJSFService.recordWeightVolumeFlow(weightVolumeEntity);

    }

    @Override
    public InvokeResult<Boolean> weightVolumeRuleCheck(WeightVolumeRuleCheckDto condition) {
        InvokeResult<Boolean> result = new  InvokeResult<Boolean>();
        // 基础校验
        basicVerification(condition,result);
        if(!result.codeSuccess()){
            return result;
        }
        WeightVolumeRuleConstant weightVolumeRuleConstant;
        try {
            weightVolumeRuleConstant = JsonHelper.fromJson(uccPropertyConfiguration.getWeightVolumeRuleStandard(), WeightVolumeRuleConstant.class);
        }catch (Exception e){
            logger.error("获取ucc配置异常!",e);
            weightVolumeRuleConstant = JsonHelper.fromJson(weightVolumeRuleConfig, WeightVolumeRuleConstant.class);
        }
        // dws校验
        if(dwsWeightVolumeCheck(condition,weightVolumeRuleConstant,result)){
            return result;
        }
        Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(WaybillUtil.getWaybillCode(condition.getBarCode()));
        // 校验处理
        weightVolumeRuleCheckHandler(condition,weightVolumeRuleConstant,waybill,result);
        return result;
    }

    private boolean dwsWeightVolumeCheck(WeightVolumeRuleCheckDto condition,WeightVolumeRuleConstant weightVolumeRuleConstant, InvokeResult<Boolean> result) {
        if(!Objects.equals(FromSourceEnum.DMS_AUTOMATIC_MEASURE.name(),condition.getSourceCode())){
            return false;
        }
        StringBuilder hintMessage = new StringBuilder();
        // 重量标准：大于1000kg按1000kg记录
        int weightMaxLimitC = weightVolumeRuleConstant.getWeightMaxLimitC();
        if(condition.getWeight() > weightMaxLimitC){
            setNextRowChar(hintMessage);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_2,weightMaxLimitC,weightMaxLimitC));
        }
        // 边长标准：大于3m按3m记录
        int sideMaxLengthC = weightVolumeRuleConstant.getSideMaxLengthC();
        if(condition.getLength() > sideMaxLengthC
                || condition.getWidth() > sideMaxLengthC || condition.getHeight() > sideMaxLengthC){
            setNextRowChar(hintMessage);
            String sideMaxLengthCStr = keepDigitCompute(sideMaxLengthC, WeightVolumeRuleConstant.CM_M_MAGNIFICATION, Constants.NUMBER_ZERO);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_0,sideMaxLengthCStr));
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_RECORD,sideMaxLengthCStr));
        }
        // 体积标准：大于4m3按4m3记录
        int volumeMaxLimitRecord = weightVolumeRuleConstant.getVolumeMaxLimitRecord();
        if(condition.getVolume() > volumeMaxLimitRecord){
            setNextRowChar(hintMessage);
            String volumeMaxLimitRecordStr = keepDigitCompute(volumeMaxLimitRecord, WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION, Constants.NUMBER_ZERO);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_1,volumeMaxLimitRecordStr));
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_RECORD,volumeMaxLimitRecordStr));
        }

        // 设置提示信息
        if (StringUtils.isNotEmpty(hintMessage.toString())){
            result.hintMessage(hintMessage.toString());
        }
        return true;
    }

    protected abstract void weightVolumeRuleCheckHandler(WeightVolumeRuleCheckDto condition,WeightVolumeRuleConstant weightVolumeRuleConstant,
                                                         Waybill waybill,InvokeResult<Boolean> result);

    /**
     * 基础校验
     * @param condition
     * @param result
     */
    private void basicVerification(WeightVolumeRuleCheckDto condition, InvokeResult<Boolean> result) {
        if(StringUtils.isEmpty(condition.getBarCode())){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_0);
            return;
        }
        if(condition.getWeight() == null
                || condition.getLength() == null || condition.getWidth() == null
                || condition.getHeight() == null || condition.getVolume() == null){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE);
            return;
        }
        if(Objects.equals(condition.getCheckWeight(),true) && condition.getWeight() <= Constants.DOUBLE_ZERO){
            result.parameterError(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_1);
        }
    }

    /**
     * C网特殊规则校验
     *
     * @param condition
     * @param result
     */
    protected void checkCInternetRule(WeightVolumeRuleCheckDto condition,WeightVolumeRuleConstant weightVolumeRuleConstant,
                                      InvokeResult<Boolean> result){
        double length = condition.getLength();
        double width = condition.getWidth();
        double height = condition.getHeight();
        double volume = condition.getVolume();
        if(volume == Constants.DOUBLE_ZERO){
            volume = length * width * height;
        }
        double weight = condition.getWeight();
        // 1、确认提示信息
        StringBuilder confirmMessage = new StringBuilder();
        // 边长标准值
        int sideMaxLengthC = weightVolumeRuleConstant.getSideMaxLengthC();
        if(length > sideMaxLengthC || width > sideMaxLengthC || height > sideMaxLengthC){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_0,
                    keepDigitCompute(sideMaxLengthC,WeightVolumeRuleConstant.CM_M_MAGNIFICATION,Constants.NUMBER_ZERO)));
        }
        // 体积标准值
        int volumeMaxLimit = weightVolumeRuleConstant.getVolumeMaxLimitC();
        if(volume > volumeMaxLimit){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_1,
                    keepDigitCompute(volumeMaxLimit,WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION,Constants.CONSTANT_NUMBER_ONE)));
        }
        // 重量标准值
        if(Objects.equals(weight,Constants.DOUBLE_ZERO)){
            setNextRowChar(confirmMessage);
            confirmMessage.append(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_CONFIRM_8);
        }
        int weightMaxLimitF = weightVolumeRuleConstant.getWeightMaxLimitCF();
        int weightMaxLimitS = weightVolumeRuleConstant.getWeightMaxLimitCS();
        if(weight >= weightMaxLimitF && weight <= weightMaxLimitS){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_4,weightMaxLimitF));
        }
        if(weight > weightMaxLimitS){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_2,weightMaxLimitS,weightMaxLimitS));
        }
        // 泡重比、泡重比倍数标准值
        int foamWeightRatio = weightVolumeRuleConstant.getFoamWeightRatioC();
        int foamWeightRatioMultiple = weightVolumeRuleConstant.getFoamWeightRatioMultiple();
        if(volume > foamWeightRatio * foamWeightRatioMultiple * weight){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_3,foamWeightRatio,foamWeightRatioMultiple));
        }

        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    /**
     * B网特殊规则校验
     * @param condition
     * @param result
     */
    protected void checkBInternetRule(WeightVolumeRuleCheckDto condition,WeightVolumeRuleConstant weightVolumeRuleConstant,
                                      Waybill waybill,InvokeResult<Boolean> result){
        double length = condition.getLength();
        double width = condition.getWidth();
        double height = condition.getHeight();
        double volume = condition.getVolume();
        if(volume == Constants.DOUBLE_ZERO){
            volume = length * width * height;
        }
        double weight = condition.getWeight();

        int packNum = waybill == null ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber() == null
                ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber();
        if(Objects.equals(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name(),condition.getBusinessType())){
            // 以包裹维度比较
            weight = weight / packNum;
            volume = volume / packNum;
        }

        // 1、强制提示信息
        StringBuilder forceMessage;
        // 重量校验：是否大于5000kg
        int weightMaxLimitB = weightVolumeRuleConstant.getWeightMaxLimitB();
        if(weight > weightMaxLimitB){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_3, weightMaxLimitB))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }
        // 体积校验：是否大于5m3
        int volumeMaxLimit = weightVolumeRuleConstant.getVolumeMaxLimitB();
        if(volume > volumeMaxLimit){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_1,
                    keepDigitCompute(volumeMaxLimit,WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION,Constants.NUMBER_ZERO)))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }
        // 泡重比校验：是否大于7800
        int foamWeightRatioB = weightVolumeRuleConstant.getFoamWeightRatioB();
        if(weight * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION > foamWeightRatioB * volume){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_2,foamWeightRatioB))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }

        // 2、确认提示信息
        StringBuilder confirmMessage = new StringBuilder();
        // 边长标准值
        int sideMaxLengthB = weightVolumeRuleConstant.getSideMaxLengthB();
        if(length > sideMaxLengthB || width > sideMaxLengthB || height > sideMaxLengthB){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_0,
                    keepDigitCompute(sideMaxLengthB,WeightVolumeRuleConstant.CM_M_MAGNIFICATION,Constants.NUMBER_ZERO)));
        }
        if(Objects.equals(weight,Constants.DOUBLE_ZERO)){
            setNextRowChar(confirmMessage);
            confirmMessage.append(WeightVolumeRuleConstant.RESULT_BASIC_MESSAGE_CONFIRM_8);
        }
        int weightMaxLimitConfirmB = weightVolumeRuleConstant.getWeightMaxLimitConfirmB();
        int volumeMaxLimitConfirmB = weightVolumeRuleConstant.getVolumeMaxLimitConfirmB();
        if(weight > weightMaxLimitConfirmB || volume > volumeMaxLimitConfirmB){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_1,
                    weightMaxLimitConfirmB,volumeMaxLimitConfirmB/WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION));
        }
        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    /**
     * 计算并保留几位小数
     * @param num 数值
     * @param ratio 比率
     * @param ratio 保留位数
     * @return
     */
    private String keepDigitCompute(int num,int ratio,int digit){
        try {
            String format;
            if(digit == Constants.NUMBER_ZERO){
                return String.valueOf(num / ratio);
            }else if(digit == Constants.CONSTANT_NUMBER_ONE){
                format = "#0.0";
            }else if(digit == Constants.CONSTANT_NUMBER_TWO){
                format = "#0.00";
            }else {
                return Constants.EMPTY_FILL;
            }
            DecimalFormat doubleFormat = new DecimalFormat(format);
            return doubleFormat.format(Double.parseDouble(String.valueOf(num))/ratio);
        }catch (Exception e){
            logger.error("保留小数异常!");
        }
        return Constants.EMPTY_FILL;
    }

    /***
     * 设置换行符
     * @param confirmMessage
     */
    private void setNextRowChar(StringBuilder confirmMessage) {
        if(StringUtils.isEmpty(confirmMessage.toString())){
            return;
        }
        confirmMessage.append(Constants.LINE_NEXT_CHAR);
    }

    /**
     * 设置确认类型结尾提示语
     * @param confirmMessage
     * @param result
     */
    private void setEndConfirmMessage(StringBuilder confirmMessage, InvokeResult<Boolean> result) {
        if(StringUtils.isEmpty(confirmMessage.toString())){
            return;
        }
        confirmMessage.append(Constants.LINE_NEXT_CHAR);
        confirmMessage.append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM);
        result.confirmMessage(confirmMessage.toString());
    }
}
