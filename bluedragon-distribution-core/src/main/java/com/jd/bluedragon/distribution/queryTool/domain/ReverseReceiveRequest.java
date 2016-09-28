package com.jd.bluedragon.distribution.queryTool.domain;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2016/9/19.
 */
public class ReverseReceiveRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    private String sendCode;//批次号
    private String packageBarCode;//包裹编码
    private Integer businessType;//业务类型
    private String operator;//操作人
    private Integer canReceive;//是否可收

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getPackageBarCode() {
        return packageBarCode;
    }

    public void setPackageBarCode(String packageBarCode) {
        this.packageBarCode = packageBarCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getCanReceive() {
        return canReceive;
    }

    public void setCanReceive(Integer canReceive) {
        this.canReceive = canReceive;
    }
}
