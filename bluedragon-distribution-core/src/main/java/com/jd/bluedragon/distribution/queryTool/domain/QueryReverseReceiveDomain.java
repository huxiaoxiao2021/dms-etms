package com.jd.bluedragon.distribution.queryTool.domain;

import java.util.Date;

/**
 * 对应reverse_receive表
 * Created by xumei1 on 2016/8/30.
 */

public class QueryReverseReceiveDomain {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;
    private String fingerprint;
    private Date operateTime;
    private String operator;
    private Integer canReceive;
    private Integer rejectCode;
    private Date createTime;
    private Date updateTime;
    private String rejectMessage;
    private Integer yn;
    private String operatorCode;
    private String sendCode;
    private String packageBarCode;
    private Integer businessType;

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getPackageBarCode() {
        return packageBarCode;
    }

    public void setPackageBarCode(String packageBarCode) {
        this.packageBarCode = packageBarCode;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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

    public Integer getRejectCode() {
        return rejectCode;
    }

    public void setRejectCode(Integer rejectCode) {
        this.rejectCode = rejectCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRejectMessage() {
        return rejectMessage;
    }

    public void setRejectMessage(String rejectMessage) {
        this.rejectMessage = rejectMessage;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

}
