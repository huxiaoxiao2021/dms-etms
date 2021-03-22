package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

import java.io.Serializable;

public class WaybillWeightVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /*运单号或运单下包裹号*/
    private String codeStr;

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
    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 错误编码
     */
    private Integer errorCode;

    /**
     * 存放当前索引 供前台使用
     */
    private Integer key;


    /*是否可强制提交 0否 1是*/
    private Integer canSubmit;

    @Override
    public String toString()
    {
        return "WaybillWeightVO{" +
                "codeStr='" + codeStr + '\'' +
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getCanSubmit() {
        return canSubmit;
    }

    public void setCanSubmit(Integer canSubmit) {
        this.canSubmit = canSubmit;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public WaybillWeightVO(String codeStr, Double weight, Double volume, Integer status, Integer operatorId, String operatorName, Integer operatorSiteCode, String operatorSiteName, long operateTimeMillis)
    {

        this.codeStr = codeStr;
        this.weight = weight;
        this.volume = volume;
        this.status = status;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.operatorSiteCode = operatorSiteCode;
        this.operatorSiteName = operatorSiteName;
        this.operateTimeMillis = operateTimeMillis;
    }

    public WaybillWeightVO()
    {

    }
}
