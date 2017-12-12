package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

/**
 * 运单称重 用于发送mq消息给运单
 */
public class WaybillWeightDTO
{
    /*运单号*/
    private String waybillCode;

    /*重量 单位kg*/
    private Double weight;

    /*体积 单位立方米*/
    private Double volume;

    /*是否存在运单信息 10 存在 20 不存在*/
    private Integer status;

    /*操作人 id 同基础资料id*/
    private Integer operatorId;

    /*操作人姓名*/
    private String operatorName;

    /*操作人所在分拣中心SiteCode*/
    private Integer operatorSiteCode;

    /*操作人所在分拣中心名称*/
    private String operatorSiteName;

    /*操作时间*/
    private long operateTimeMillis;

    @Override
    public String toString()
    {
        return "WaybillWeightDTO{" +
                "waybillCode='" + waybillCode + '\'' +
                ", weight=" + weight +
                ", volume=" + volume +
                ", status=" + status +
                ", operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                ", operatorSiteCode=" + operatorSiteCode +
                ", operatorSiteName='" + operatorSiteName + '\'' +
                ", operateTimeMillis=" + operateTimeMillis +
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

    public Integer getOperatorId()
    {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId)
    {
        this.operatorId = operatorId;
    }

    public String getOperatorName()
    {
        return operatorName;
    }

    public void setOperatorName(String operatorName)
    {
        this.operatorName = operatorName;
    }

    public Integer getOperatorSiteCode()
    {
        return operatorSiteCode;
    }

    public void setOperatorSiteCode(Integer operatorSiteCode)
    {
        this.operatorSiteCode = operatorSiteCode;
    }

    public String getOperatorSiteName()
    {
        return operatorSiteName;
    }

    public void setOperatorSiteName(String operatorSiteName)
    {
        this.operatorSiteName = operatorSiteName;
    }

    public long getOperateTimeMillis()
    {
        return operateTimeMillis;
    }

    public void setOperateTimeMillis(long operateTimeMillis)
    {
        this.operateTimeMillis = operateTimeMillis;
    }

    public WaybillWeightDTO(String waybillCode, Double weight, Double volume, Integer status, Integer operatorId, String operatorName, Integer operatorSiteCode, String operatorSiteName, long operateTimeMillis)
    {

        this.waybillCode = waybillCode;
        this.weight = weight;
        this.volume = volume;
        this.status = status;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorSiteCode = operatorSiteCode;
        this.operatorSiteName = operatorSiteName;
        this.operateTimeMillis = operateTimeMillis;
    }

    public WaybillWeightDTO()
    {

    }
}
