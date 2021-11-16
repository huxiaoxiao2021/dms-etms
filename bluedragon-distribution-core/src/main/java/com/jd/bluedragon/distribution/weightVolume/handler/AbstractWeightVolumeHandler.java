package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeContext;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        try {
            // 初始化上下文
            WeightVolumeContext weightVolumeContext = initWeightVolumeContext(condition);
            // 是否需要校验处理
            if(!checkIsNeedDeal(weightVolumeContext)){
                return result;
            }
            // 基础校验
            basicVerification(weightVolumeContext, result);
            if(!result.codeSuccess()){
                return result;
            }
            // dws校验
            if(dwsWeightVolumeCheck(weightVolumeContext, result)){
                return result;
            }
            // 校验处理
            weightVolumeRuleCheckHandler(weightVolumeContext, result);
        }catch (Exception e){
            // 为了不影响前台正常操作，异常只记录错误日志，但是返回成功
            logger.error("根据条件：{}校验称重量方规则异常!", JsonHelper.toJson(condition));
        }
        return result;
    }

    /**
     * 是否需要校验
     *  hit：
     *      1、信任商家不校验重量体积（站点平台除外）
     * @param weightVolumeContext
     * @return
     */
    private boolean checkIsNeedDeal(WeightVolumeContext weightVolumeContext) {
        if(!Objects.equals(weightVolumeContext.getSourceCode(), FromSourceEnum.DMS_CLIENT_SITE_PLATE_PRINT.name())){
            if(BusinessHelper.isTrust(weightVolumeContext.getWaybill().getWaybillSign())){
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化称重量方上下文
     * @param condition
     * @return
     */
    private WeightVolumeContext initWeightVolumeContext(WeightVolumeRuleCheckDto condition) {
        // 初始化基本数据
        WeightVolumeContext weightVolumeContext = new WeightVolumeContext();
        BeanUtils.copyProperties(condition, weightVolumeContext);
        if(condition.getWeight() == null){
            weightVolumeContext.setWeight(Constants.DOUBLE_ZERO);
        }
        if(condition.getLength() == null){
            weightVolumeContext.setLength(Constants.DOUBLE_ZERO);
        }
        if(condition.getWidth() == null){
            weightVolumeContext.setWidth(Constants.DOUBLE_ZERO);
        }
        if(condition.getHeight() == null){
            weightVolumeContext.setHeight(Constants.DOUBLE_ZERO);
        }
        if(condition.getVolume() == null || Objects.equals(condition.getVolume(), Constants.DOUBLE_ZERO)){
            weightVolumeContext.setVolume(weightVolumeContext.getLength() * weightVolumeContext.getWidth() * weightVolumeContext.getHeight());
        }
        // 初始化运单数据
        if(WaybillUtil.isWaybillCode(condition.getBarCode()) || WaybillUtil.isPackageCode(condition.getBarCode())){
            Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(WaybillUtil.getWaybillCode(condition.getBarCode()));
            if(waybill == null){
                throw new RuntimeException("无运单信息!");
            }
            weightVolumeContext.setWaybill(waybill);
        }
        // 设置称重量方规则
        weightVolumeContext.setWeightVolumeRuleConstant(JsonHelper.fromJson(uccPropertyConfiguration.getWeightVolumeRuleStandard(), WeightVolumeRuleConstant.class));
        return weightVolumeContext;
    }

    private boolean dwsWeightVolumeCheck(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result) {
        if(!Objects.equals(FromSourceEnum.DMS_AUTOMATIC_MEASURE.name(), weightVolumeContext.getSourceCode())){
            return false;
        }
        WeightVolumeRuleConstant weightVolumeRuleConstant = weightVolumeContext.getWeightVolumeRuleConstant();
        StringBuilder hintMessage = new StringBuilder();
        // 重量标准：大于1000kg按1000kg记录
        int weightMaxLimitC = weightVolumeRuleConstant.getWeightMaxLimitCS();
        if(weightVolumeContext.getWeight() > weightMaxLimitC){
            setNextRowChar(hintMessage);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_2,weightMaxLimitC,weightMaxLimitC));
        }
        // 边长标准：大于3m按3m记录
        int sideMaxLengthC = weightVolumeRuleConstant.getSideMaxLengthC();
        if(weightVolumeContext.getLength() > sideMaxLengthC
                || weightVolumeContext.getWidth() > sideMaxLengthC || weightVolumeContext.getHeight() > sideMaxLengthC){
            setNextRowChar(hintMessage);
            String sideMaxLengthCStr = keepDigitCompute(sideMaxLengthC, WeightVolumeRuleConstant.CM_M_MAGNIFICATION, Constants.NUMBER_ZERO);
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_0,sideMaxLengthCStr));
            hintMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_RECORD,sideMaxLengthCStr));
        }
        // 体积标准：大于4m3按4m3记录
        int volumeMaxLimitRecord = weightVolumeRuleConstant.getVolumeMaxLimitRecord();
        if(weightVolumeContext.getVolume() > volumeMaxLimitRecord){
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

    protected abstract void weightVolumeRuleCheckHandler(WeightVolumeContext weightVolumeContext,InvokeResult<Boolean> result);


    protected abstract void basicVerification(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result);

    /**
     * C网特殊规则校验
     *  hit：长宽高和体积可能不存在
     * @param weightVolumeContext
     * @param result
     */
    protected void checkCInternetRule(WeightVolumeContext weightVolumeContext, InvokeResult<Boolean> result){
        double length = weightVolumeContext.getLength();
        double width = weightVolumeContext.getWidth();
        double height = weightVolumeContext.getHeight();
        double volume = weightVolumeContext.getVolume();
        double weight = weightVolumeContext.getWeight();
        WeightVolumeRuleConstant weightVolumeRuleConstant = weightVolumeContext.getWeightVolumeRuleConstant();
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
        if(volume > Constants.DOUBLE_ZERO && weight > Constants.DOUBLE_ZERO && volume > foamWeightRatio * foamWeightRatioMultiple * weight){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_C_3,foamWeightRatio,foamWeightRatioMultiple));
        }

        // 设置提示结尾提示语
        setEndConfirmMessage(confirmMessage,result);
    }

    /**
     * B网特殊规则校验
     *  hit：长宽高和体积可能不存在
     * @param weightVolumeContext
     * @param result
     */
    protected void checkBInternetRule(WeightVolumeContext weightVolumeContext,InvokeResult<Boolean> result){
        double length = weightVolumeContext.getLength();
        double width = weightVolumeContext.getWidth();
        double height = weightVolumeContext.getHeight();
        double volume = weightVolumeContext.getVolume();
        double weight = weightVolumeContext.getWeight();
        WeightVolumeRuleConstant weightVolumeRuleConstant = weightVolumeContext.getWeightVolumeRuleConstant();
        Waybill waybill = weightVolumeContext.getWaybill();

        int packNum = waybill == null ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber() == null
                ? Constants.CONSTANT_NUMBER_ONE : waybill.getGoodNumber();
        if(Objects.equals(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name(), weightVolumeContext.getBusinessType())){
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
        if(volume > Constants.DOUBLE_ZERO && weight * WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION > foamWeightRatioB * volume){
            forceMessage = new StringBuilder().append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B_2,foamWeightRatioB))
                    .append(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_FORCE_B);
            result.parameterError(forceMessage.toString());
            return;
        }

        // 2、确认提示信息
        StringBuilder confirmMessage = new StringBuilder();
        // 边长标准值：大于3m
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
        // 重量及体积：200kg/1包裹 或 1立方/1包裹
        int weightMaxLimitConfirmB = weightVolumeRuleConstant.getWeightMaxLimitConfirmB();
        int volumeMaxLimitConfirmB = weightVolumeRuleConstant.getVolumeMaxLimitConfirmB();
        if(weight > weightMaxLimitConfirmB || volume > volumeMaxLimitConfirmB){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_1,
                    weightMaxLimitConfirmB,volumeMaxLimitConfirmB/WeightVolumeRuleConstant.CM3_M3_MAGNIFICATION));
        }
        // 泡重比：大于168小于330
        int foamWeightRatioConfirmFloorB = weightVolumeRuleConstant.getFoamWeightRatioConfirmFloorB();
        int foamWeightRatioConfirmCeilingB = weightVolumeRuleConstant.getFoamWeightRatioConfirmCeilingB();
        if(volume > Constants.DOUBLE_ZERO && weight > volume * foamWeightRatioConfirmFloorB && weight < volume * foamWeightRatioConfirmCeilingB){
            setNextRowChar(confirmMessage);
            confirmMessage.append(String.format(WeightVolumeRuleConstant.RESULT_SPECIAL_MESSAGE_CONFIRM_B_3,
                    foamWeightRatioConfirmFloorB,foamWeightRatioConfirmCeilingB));
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
