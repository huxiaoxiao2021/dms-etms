package com.jd.bluedragon.distribution.enterpriseDistribution.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;

/**
 * @Description 质检报表查询
 * @Author chenjunyan
 * @Date 2022/6/15
 */
public class QualityInspectionQueryCondition extends BasePagerCondition {

    /**
     * 运单号
     */
    private String waybillNo;

    /**
     * 状态
     */
    private Integer optStatus;

    /**
     * 异常原因
     */
    private Integer exceptionReason;

    /**
     * 操作人erp
     */
    private String updateUser;

    /**
     * 创建开始时间
     */
    private Date createStartTime;

    /**
     * 创建结束时间
     */
    private Date createEndTime;

    /**
     * 更新开始时间
     */
    private Date updateStartTime;

    /**
     * 更新结束时间
     */
    private Date updateEndTime;

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Integer getOptStatus() {
        return optStatus;
    }

    public void setOptStatus(Integer optStatus) {
        this.optStatus = optStatus;
    }

    public Integer getExceptionReason() {
        return exceptionReason;
    }

    public void setExceptionReason(Integer exceptionReason) {
        this.exceptionReason = exceptionReason;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(Date createStartTime) {
        this.createStartTime = createStartTime;
    }

    public Date getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(Date createEndTime) {
        this.createEndTime = createEndTime;
    }

    public Date getUpdateStartTime() {
        return updateStartTime;
    }

    public void setUpdateStartTime(Date updateStartTime) {
        this.updateStartTime = updateStartTime;
    }

    public Date getUpdateEndTime() {
        return updateEndTime;
    }

    public void setUpdateEndTime(Date updateEndTime) {
        this.updateEndTime = updateEndTime;
    }
}
