package com.jd.bluedragon.distribution.siteRetake.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 更新
 * @date 2018年08月02日 17时:12分
 */
public class SiteRetakeOperation {
    private  String waybillCode;
    private Integer status;
    private String remark;
    private Integer operatorId;//配送员id
    private Integer endReason;//终止原因(再取原因)
    private Date requiredStartTime;
    private Date requiredEndTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEndReason() {
        return endReason;
    }

    public void setEndReason(Integer endReason) {
        this.endReason = endReason;
    }

    public Date getRequiredStartTime() {
        return requiredStartTime;
    }

    public void setRequiredStartTime(Date requiredStartTime) {
        this.requiredStartTime = requiredStartTime;
    }

    public Date getRequiredEndTime() {
        return requiredEndTime;
    }

    public void setRequiredEndTime(Date requiredEndTime) {
        this.requiredEndTime = requiredEndTime;
    }
}
