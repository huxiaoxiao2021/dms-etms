package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

import java.io.Serializable;

public class WaybillWeightDTO implements Serializable
{

    private String waybillCode;
    private Double weight;
    private Double volume;
    private Integer status;

    public WaybillWeightDTO(String waybillCode, Double weight, Double volume, Integer status)
    {
        this.waybillCode = waybillCode;
        this.weight = weight;
        this.volume = volume;
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "WaybillWeightDTO{" +
                "waybillCode='" + waybillCode + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", status=" + status +
                '}';
    }

    public String getWaybillCode()
    {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode)
    {
        this.waybillCode = waybillCode;
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

    public WaybillWeightDTO()
    {

    }
}
