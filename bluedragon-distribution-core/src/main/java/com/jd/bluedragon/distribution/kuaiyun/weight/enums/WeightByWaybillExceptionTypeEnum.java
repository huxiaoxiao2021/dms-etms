package com.jd.bluedragon.distribution.kuaiyun.weight.enums;

import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;

/**
 * 运单称重自定义异常类型枚举
 * luyue5
 */
public enum WeightByWaybillExceptionTypeEnum
{
    //输入未知编码
    UnknownCodeException(false,WeightByWaybillExceptionTypeEnum.UnknownCodeExceptionMessage),
    //输入编码非包裹号
    NotPackageCodeException(false,WeightByWaybillExceptionTypeEnum.NotPackageCodeMessage),

    //运单验证服务不可用
    WaybillServiceNotAvailableException(true,WeightByWaybillExceptionTypeEnum.WaybillServiceNotAvailableExceptionMessage),

    //未找到对应运单
    WaybillNotFindException(false,WeightByWaybillExceptionTypeEnum.WaybillNotFindExceptionMessage),

    //不需要称重
    WaybillNoNeedWeightException(true,WeightByWaybillExceptionTypeEnum.WaybillNoNeedWeightExceptionMessage),

    //KA运单 必须按包裹维度录入重量体积
    WaybillNeedPackageWeightException(true,WeightByWaybillExceptionTypeEnum.WaybillNeedPackageWeightMessage),

    //运单称重对象转换json失败
    WaybillWeightVOConvertExcetion(true,WeightByWaybillExceptionTypeEnum.WaybillWeightVOConvertExcetionMessage),

    //由于网络等其他原因，MQ服务不可用，MQ发送过程出错
    MQServiceNotAvailableException(false,WeightByWaybillExceptionTypeEnum.MQServiceNotAvailableMessage),

    //通过非界面操作方式调用运单称重录入方法
    InvalidMethodInvokeException(true,WeightByWaybillExceptionTypeEnum.InvalidMethodInvokeExceptionMessage),

    //已经妥投的不允许再进行操作
    WaybillFinishedException(true,WeightByWaybillExceptionTypeEnum.WaybillFinishedExceptionMessage),

    //包裹号不存在
    NoPackageException(true,WeightByWaybillExceptionTypeEnum.NoPackageMessage),

    //不支持按包裹维度批量导入
    NotSupportUpWeightByPackageException(true,WeightByWaybillExceptionTypeEnum.NotSupportUpWeightByPackageMessage),
    //C网运单不支持在快运处录入复重量方
    NotSupportUpCWaybillException(true,WeightByWaybillExceptionTypeEnum.NotSupportUpCWaybillMessage),

    // 集配场地揽收后不能称重
    JPForbidWeightAfterLLException(true, WeightVolumeRuleConstant.RESULT_WEIGHT_INTERCEPT_AFTER_LL);

    public static final String UnknownCodeExceptionMessage = "所输入的编码格式有误：既不符合运单号也不符合包裹号编码规则";
    public static final String WaybillServiceNotAvailableExceptionMessage = "调取运单系统失败，运单查询接口不可用";
    public static final String WaybillNotFindExceptionMessage = "调取运单系统成功，但未查询到运单数据";
    public static final String WaybillWeightVOConvertExcetionMessage = "调取运单称重量方对象转换JSON失败，请检查原因";
    public static final String MQServiceNotAvailableMessage = "运单称重信息MQ发送失败，将转为task异步重试";
    public static final String InvalidMethodInvokeExceptionMessage = "遭遇非界面操作方式调用运单称重录入方法";
    public static final String WaybillNoNeedWeightExceptionMessage = "此单为信任商家运单，不进行称重量方";
    public static final String WaybillFinishedExceptionMessage = "此运单为妥投状态，禁止操作此功能，请检查单号是否正确";
    public static final String WaybillNeedPackageWeightMessage ="此运单为大件KA运单，请按包裹维度批量录入重量体积";
    public static final String NoPackageMessage ="没有包裹信息";
    public static final String NotSupportUpWeightByPackageMessage ="不支持按包裹维度批量导入";
    public static final String NotPackageCodeMessage ="所输入的编码格式有误:不符合包裹号编码规则";
    public static final String NotSupportUpCWaybillMessage = "不支持C网运单";
    public boolean shouldBeThrowToTop = false;
    public String exceptionMessage = null;

    WeightByWaybillExceptionTypeEnum(boolean shouldBeThrowToTop, String exceptionMessage)
    {
        this.shouldBeThrowToTop = shouldBeThrowToTop;
        this.exceptionMessage = exceptionMessage;
    }

    WeightByWaybillExceptionTypeEnum(boolean _shouldBeThrowToTop)
    {
        this.shouldBeThrowToTop = _shouldBeThrowToTop;
    }
}
