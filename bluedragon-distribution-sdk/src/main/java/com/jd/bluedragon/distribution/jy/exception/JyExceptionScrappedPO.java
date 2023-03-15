package com.jd.bluedragon.distribution.jy.exception;

import java.util.Date;

/**
 * 异常-报废实体po
 */
public class JyExceptionScrappedPO {

    private Long id;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 站点id
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 上保时间
     */
    private Date submitTime;
    /**
     * 异常类型 1：生鲜报废
     */
    private Integer exceptionType;

    /**
     * 物品照片 多个逗号分割
     */
    private String goodsImageUrl;

    /**
     * 证明照片 多个逗号分割
     */
    private String certifyImageUrl;

    /**
     * 第一审核人erp
     */
    private String firstChecker;

    /**
     * 第一审核人审核状态 1：待审批 2：审批通过 3：审批驳回
     */
    private Integer firstCheckStatus;

    /**
     * 审核第一人审核时间
     */
    private Date firstCheckTime;

    /**
     * 第二审核人erp
     */
    private String secondChecker;

    /**
     * 第二审核人审核状态 1：待审批 2：审批通过 3：审批驳回
     */
    private Integer secondCheckStatus;


    /**
     * 审核第二人审核时间
     */
    private Date secondCheckTime;

    /**
     * 第三审核人erp
     */
    private String thirdChecker;

    /**
     * 第三审核人审核状态 1：待审批 2：审批通过 3：审批驳回
     */
    private Integer thirdCheckStatus;

    /**
     * 审核第三人审核时间
     */
    private Date thirdCheckTime;

    /**
     * 删除标识
     */
    private Byte yn;

    private Date createTime;

    private String createErp;

    private Date updateTime;

    private String updateErp;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Integer exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
    }

    public String getCertifyImageUrl() {
        return certifyImageUrl;
    }

    public void setCertifyImageUrl(String certifyImageUrl) {
        this.certifyImageUrl = certifyImageUrl;
    }

    public String getFirstChecker() {
        return firstChecker;
    }

    public void setFirstChecker(String firstChecker) {
        this.firstChecker = firstChecker;
    }

    public Integer getFirstCheckStatus() {
        return firstCheckStatus;
    }

    public void setFirstCheckStatus(Integer firstCheckStatus) {
        this.firstCheckStatus = firstCheckStatus;
    }

    public String getSecondChecker() {
        return secondChecker;
    }

    public void setSecondChecker(String secondChecker) {
        this.secondChecker = secondChecker;
    }

    public Integer getSecondCheckStatus() {
        return secondCheckStatus;
    }

    public void setSecondCheckStatus(Integer secondCheckStatus) {
        this.secondCheckStatus = secondCheckStatus;
    }

    public String getThirdChecker() {
        return thirdChecker;
    }

    public void setThirdChecker(String thirdChecker) {
        this.thirdChecker = thirdChecker;
    }

    public Integer getThirdCheckStatus() {
        return thirdCheckStatus;
    }

    public void setThirdCheckStatus(Integer thirdCheckStatus) {
        this.thirdCheckStatus = thirdCheckStatus;
    }

    public Byte getYn() {
        return yn;
    }

    public void setYn(Byte yn) {
        this.yn = yn;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateErp() {
        return updateErp;
    }

    public void setUpdateErp(String updateErp) {
        this.updateErp = updateErp;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Date getFirstCheckTime() {
        return firstCheckTime;
    }

    public void setFirstCheckTime(Date firstCheckTime) {
        this.firstCheckTime = firstCheckTime;
    }

    public Date getSecondCheckTime() {
        return secondCheckTime;
    }

    public void setSecondCheckTime(Date secondCheckTime) {
        this.secondCheckTime = secondCheckTime;
    }

    public Date getThirdCheckTime() {
        return thirdCheckTime;
    }

    public void setThirdCheckTime(Date thirdCheckTime) {
        this.thirdCheckTime = thirdCheckTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }
}