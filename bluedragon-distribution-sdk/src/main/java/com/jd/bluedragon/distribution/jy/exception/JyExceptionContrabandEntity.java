package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;
import java.util.Date;

public class JyExceptionContrabandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 业务id
     */
    private String bizId;
    /**
     * 站点code
     */
    private Integer siteCode;
    /**
     * 站点name
     */

    private String siteName;

    /**
     * 异常条码
     */
    private String barCode;

    /**
     * 违禁品类型（1：扣减 2.航空转陆运 3.退回）
     */
    private Integer contrabandType;
    /**
     * 客服反馈类型（1：更换包装下传 2：再派、未联系上、下传 3：破损已理赔、退回 4：报废 5：补单、补差）
     */
    private Integer feedBackType;

    /**
     * 货物情况
     */
    private String description;

    private Integer createUserId;

    private String createErp;

    private String createStaffName;

    private Date createTime;

    private String updateErp;

    private String updateStaffName;

    private Date updateTime;

    private Date ts;

    private Boolean yn;

    /**
     * 一级异常原因
     */
    private String firstReasonLevel;

    /**
     * 二级异常原因
     */
    private String secondReasonLevel;

    /**
     * 三级异常原因
     */
    private String thirdReasonLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getContrabandType() {
        return contrabandType;
    }

    public void setContrabandType(Integer contrabandType) {
        this.contrabandType = contrabandType;
    }

    public Integer getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(Integer feedBackType) {
        this.feedBackType = feedBackType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public String getCreateStaffName() {
        return createStaffName;
    }

    public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateErp() {
        return updateErp;
    }

    public void setUpdateErp(String updateErp) {
        this.updateErp = updateErp;
    }

    public String getUpdateStaffName() {
        return updateStaffName;
    }

    public void setUpdateStaffName(String updateStaffName) {
        this.updateStaffName = updateStaffName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getFirstReasonLevel() {
        return firstReasonLevel;
    }

    public void setFirstReasonLevel(String firstReasonLevel) {
        this.firstReasonLevel = firstReasonLevel;
    }

    public String getSecondReasonLevel() {
        return secondReasonLevel;
    }

    public void setSecondReasonLevel(String secondReasonLevel) {
        this.secondReasonLevel = secondReasonLevel;
    }

    public String getThirdReasonLevel() {
        return thirdReasonLevel;
    }

    public void setThirdReasonLevel(String thirdReasonLevel) {
        this.thirdReasonLevel = thirdReasonLevel;
    }
}