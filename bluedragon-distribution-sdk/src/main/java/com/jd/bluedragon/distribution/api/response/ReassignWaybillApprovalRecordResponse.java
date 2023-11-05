package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/10/31 15:24
 * @Description:
 */
public class ReassignWaybillApprovalRecordResponse implements Serializable {

    private static final long serialVersionUID = 7896822159938321276L;
    private String provinceAgencyCode;

    private String provinceAgencyName;

    private String areaHubCode;

    private String areaHubName;

    private Integer siteCode;

    private String siteName;

    private String applicationUserErp;

    private String barCode;

    private Date submitTime;

    private Integer receiveSiteCode;

    private String receiveSiteName;

    private Integer changeSiteCode;

    private String changeSiteName;

    private Boolean changeSiteReasonType;

    private Byte checkFlag;

    private String firstChecker;

    private Byte firstCheckStatus;

    private Date firstCheckTime;

    private String secondChecker;

    private Byte secondCheckStatus;

    private Date secondCheckTime;

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

    public Byte getCheckFlag() {
        return checkFlag;
    }

    public void setCheckFlag(Byte checkFlag) {
        this.checkFlag = checkFlag;
    }

    public String getFirstChecker() {
        return firstChecker;
    }

    public void setFirstChecker(String firstChecker) {
        this.firstChecker = firstChecker;
    }

    public Byte getFirstCheckStatus() {
        return firstCheckStatus;
    }

    public void setFirstCheckStatus(Byte firstCheckStatus) {
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

    public Byte getSecondCheckStatus() {
        return secondCheckStatus;
    }

    public void setSecondCheckStatus(Byte secondCheckStatus) {
        this.secondCheckStatus = secondCheckStatus;
    }

    public Date getSecondCheckTime() {
        return secondCheckTime;
    }

    public void setSecondCheckTime(Date secondCheckTime) {
        this.secondCheckTime = secondCheckTime;
    }
}
