package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

public class WaybillWeightVO
{
    private String codeStr;
    private Double weight;
    private Double volume;
    private Integer status;

    @Override
    public String toString()
    {
        return "WaybillWeightVO{" +
                "codeStr='" + codeStr + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", status=" + status +
                '}';
    }

    public String getCodeStr()
    {
        return codeStr;
    }

    public void setCodeStr(String codeStr)
    {
        this.codeStr = codeStr;
    }

    public Double getWeight()
    {
        return weight;
    }

    public void setWeight(Double weight)
    {
        this.weight = weight;
    }

    public Double getVolume()
    {
        return volume;
    }

    public void setVolume(Double volume)
    {
        this.volume = volume;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public WaybillWeightVO()
    {

    }

    public WaybillWeightVO(String codeStr, Double weight, Double volume, Integer status)
    {

        this.codeStr = codeStr;
        this.weight = weight;
        this.volume = volume;
        this.status = status;
    }
}
