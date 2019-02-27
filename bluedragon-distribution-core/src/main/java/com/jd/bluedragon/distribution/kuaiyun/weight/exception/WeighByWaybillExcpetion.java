package com.jd.bluedragon.distribution.kuaiyun.weight.exception;


import com.jd.bluedragon.distribution.kuaiyun.weight.enums.WeightByWaybillExceptionTypeEnum;

public class WeighByWaybillExcpetion extends Exception
{
    public String exceptionMessage;
    public WeightByWaybillExceptionTypeEnum exceptionType;


    public WeighByWaybillExcpetion(String _exceptionMessage)
    {
        this.exceptionMessage = _exceptionMessage;
    }

    public WeighByWaybillExcpetion(String exceptionMessage, WeightByWaybillExceptionTypeEnum exceptionType)
    {
        this.exceptionMessage = exceptionMessage;
        this.exceptionType = exceptionType;
    }

    public WeighByWaybillExcpetion(WeightByWaybillExceptionTypeEnum exceptionType)
    {
        this.exceptionType = exceptionType;
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

    public WeightByWaybillExceptionTypeEnum getExceptionType()
    {
        return exceptionType;
    }

    public void setExceptionType(WeightByWaybillExceptionTypeEnum exceptionType)
    {
        this.exceptionType = exceptionType;
    }

    public String getExceptionMessage()
    {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage)
    {
        this.exceptionMessage = exceptionMessage;
    }
}


