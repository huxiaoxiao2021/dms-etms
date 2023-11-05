package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

public class ReassignWaybillApprovalRecordQuery implements Serializable {
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
    private Boolean changeSiteReasonType;



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

    public Boolean getChangeSiteReasonType() {
        return changeSiteReasonType;
    }

    public void setChangeSiteReasonType(Boolean changeSiteReasonType) {
        this.changeSiteReasonType = changeSiteReasonType;
    }

}