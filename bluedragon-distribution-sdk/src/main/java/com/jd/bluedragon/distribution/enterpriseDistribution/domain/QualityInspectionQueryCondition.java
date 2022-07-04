package com.jd.bluedragon.distribution.enterpriseDistribution.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

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
    private String createStartTime;

    /**
     * 创建结束时间
     */
    private String createEndTime;

    /**
     * 更新开始时间
     */
    private String updateStartTime;

    /**
     * 更新结束时间
     */
    private String updateEndTime;

    /**
     * 网点编码
     */
    private Integer siteCode;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

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

    public String getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(String createStartTime) {
        this.createStartTime = createStartTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getUpdateStartTime() {
        return updateStartTime;
    }

    public void setUpdateStartTime(String updateStartTime) {
        this.updateStartTime = updateStartTime;
    }

    public String getUpdateEndTime() {
        return updateEndTime;
    }

    public void setUpdateEndTime(String updateEndTime) {
        this.updateEndTime = updateEndTime;
    }
}
