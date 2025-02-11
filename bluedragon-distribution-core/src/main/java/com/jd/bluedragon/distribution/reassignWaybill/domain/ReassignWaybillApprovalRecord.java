package com.jd.bluedragon.distribution.reassignWaybill.domain;

import java.io.Serializable;
import java.util.Date;

public class ReassignWaybillApprovalRecord implements Serializable {

    private Long id;

    /**
     * 省区编码
     */
    private String provinceAgencyCode;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    /**
     * 枢纽编码
     */
    private String areaHubCode;

    /**
     * 枢纽名称
     */
    private String areaHubName;

    /**
     * 站点编码
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 返调度申请人erp
     */
    private String applicationUserErp;

    /**
     * 返调度单号
     */
    private String barCode;

    /**
     * 操作时间
     */
    private Date submitTime;

    /**
     * 预分拣目的站点编码
     */
    private Integer receiveSiteCode;

    /**
     * 预分拣目的站点名称
     */
    private String receiveSiteName;

    /**
     * 返调度站点编码
     */
    private Integer changeSiteCode;

    /**
     * 返调度站点名称
     */
    private String changeSiteName;

    /**
     * 返调度原因类型 1：预分拣站点无法派送 2：特殊时期管制违禁品 3：邮政拒收 4：无预分拣站点
     */
    private Integer changeSiteReasonType;


    /**
     * 审核第一人erp
     */
    private String firstChecker;

    /**
     * 第一人审核状态：1：待审批 2：审批通过 3：审批驳回
     */
    private Integer firstCheckStatus;

    /**
     * 第一审批人审批时间
     */
    private Date firstCheckTime;

    /**
     * 审核第二人erp
     */
    private String secondChecker;

    /**
     * 第二人审核状态：1：待审批 2：审批通过 3：审批驳回
     */
    private Integer secondCheckStatus;

    /**
     * 第二审批人审批时间
     */
    private Date secondCheckTime;

    /**
     * 审批完结标识 1：审批未完结 2： 审批完结
     */
    private Integer checkEndFlag;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 更新人erp
     */
    private String updateUserErp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 时间戳
     */
    private Date ts;

    /**
     * 照片链接
     */
    private String imageUrls;

    /**
     * 删除标识
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }

    public String getAreaHubName() {
        return areaHubName;
    }

    public void setAreaHubName(String areaHubName) {
        this.areaHubName = areaHubName;
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

    public String getApplicationUserErp() {
        return applicationUserErp;
    }

    public void setApplicationUserErp(String applicationUserErp) {
        this.applicationUserErp = applicationUserErp;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getChangeSiteCode() {
        return changeSiteCode;
    }

    public void setChangeSiteCode(Integer changeSiteCode) {
        this.changeSiteCode = changeSiteCode;
    }

    public String getChangeSiteName() {
        return changeSiteName;
    }

    public void setChangeSiteName(String changeSiteName) {
        this.changeSiteName = changeSiteName;
    }

    public Integer getChangeSiteReasonType() {
        return changeSiteReasonType;
    }

    public void setChangeSiteReasonType(Integer changeSiteReasonType) {
        this.changeSiteReasonType = changeSiteReasonType;
    }

    public Integer getCheckEndFlag() {
        return checkEndFlag;
    }

    public void setCheckEndFlag(Integer checkEndFlag) {
        this.checkEndFlag = checkEndFlag;
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

    public Date getFirstCheckTime() {
        return firstCheckTime;
    }

    public void setFirstCheckTime(Date firstCheckTime) {
        this.firstCheckTime = firstCheckTime;
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

    public Date getSecondCheckTime() {
        return secondCheckTime;
    }

    public void setSecondCheckTime(Date secondCheckTime) {
        this.secondCheckTime = secondCheckTime;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
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

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }
}