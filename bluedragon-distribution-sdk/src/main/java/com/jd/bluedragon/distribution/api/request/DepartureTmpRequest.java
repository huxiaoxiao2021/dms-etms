package com.jd.bluedragon.distribution.api.request;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by dudong on 2015/1/12.
 */

public class DepartureTmpRequest implements Serializable{
    private static final long serialVersionUID = 7371990075938490968L;

    /**波次号*/
    private String batchCode;
    /**批次号*/
    private String sendCode;
    /**三方运单号*/
    private String thirdWaybillCode;
    /**创建时间*/
    private Date createTime;
    /**操作时间*/
    private Date operateTime;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getThirdWaybillCode() {
        return thirdWaybillCode;
    }

    public void setThirdWaybillCode(String thirdWaybillCode) {
        this.thirdWaybillCode = thirdWaybillCode;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
