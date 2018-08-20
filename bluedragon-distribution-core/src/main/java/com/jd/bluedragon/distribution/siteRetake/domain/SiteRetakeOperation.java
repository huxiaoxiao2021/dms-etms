package com.jd.bluedragon.distribution.siteRetake.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 更新
 * @date 2018年08月02日 17时:12分
 */
public class SiteRetakeOperation {
    private  String waybillCode;
    private Integer status;//【5：揽件完成 6：揽件终止7：揽件再取 88.异常订单状态】
    private String remark;
    private Integer operatorId;//操作人id
    private String operatorName;//操作人姓名
    private Integer endReason;//终止原因(再取原因)
    private Date requiredStartTime;
    private Date requiredEndTime;
    private Integer siteCode;
    private String siteName;
    private Date operatorTime;//操作时间，不能为空值

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }
}
