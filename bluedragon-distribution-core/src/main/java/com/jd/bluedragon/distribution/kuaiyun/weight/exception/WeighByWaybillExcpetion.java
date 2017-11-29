package com.jd.bluedragon.distribution.kuaiyun.weight.exception;


import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;

public class WeighByWaybillExcpetion extends RuntimeException
{
    public String exceptionMessage;
    public WeightByWaybillExceptionTypeEnum exceptionType;


    public WeighByWaybillExcpetion(String _exceptionMessage)
    {
        this.exceptionMessage = _exceptionMessage;
    }


    @Override
    public String getMessage()
    {
        return this.exceptionMessage;
    }

    @Override
    public synchronized Throwable getCause()
    {
        return super.getCause();
    }

    @Override
    public String toString()
    {
        return "WeighByWaybillExcpetion{" +
                "exceptionMessage='" + exceptionMessage + '\'' +
                ", exceptionType=" + exceptionType +
                '}';
    }
}


