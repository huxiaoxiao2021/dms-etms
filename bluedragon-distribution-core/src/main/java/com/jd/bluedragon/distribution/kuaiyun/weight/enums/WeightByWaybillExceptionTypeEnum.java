package com.jd.bluedragon.distribution.kuaiyun.weight.enums;

public enum WeightByWaybillExceptionTypeEnum
{
    UnknownCodeException(false),                            //输入未知编码
    WaybillServiceNotAvailableException(true),              //运单验证服务不可用
    WaybillNotFindException(false);                         //未找到对应运单

    public boolean shouldBeThrowToTop = false;

    WeightByWaybillExceptionTypeEnum(boolean _shouldBeThrowToTop)
    {
        this.shouldBeThrowToTop = _shouldBeThrowToTop;
    }
}
