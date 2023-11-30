package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 运单返调度审核记录查询
 */
public class ReassignWaybillApprovalRecordQuery implements Serializable {
    private static final long serialVersionUID = 7914857479024808690L;

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

    private String barCode;

    /**
     * 操作开始时间
     */
    private Date startSubmitTime;

    /**
     * 操作结束时间
     */
    private Date endSubmitTime;

    /**
     * 返调度原因类型 1：预分拣站点无法派送 2：特殊时期管制违禁品 3：邮政拒收 4：无预分拣站点
     */
    private Integer changeSiteReasonTypeCode;

    /**
     * 第一审核人审核状态
     */
    private Integer firstCheckStatus;

    /**
     * 第二审核人审核状态
     */
    private Integer secondCheckStatus;

    /**
     * 审批完结标识或者不需要走审批标识
     */
    private Integer checkEndFlag;

    private Integer pageNumber = 1;

    private Integer pageSize = 20;

    private Integer offSet;

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

    public Date getStartSubmitTime() {
        return startSubmitTime;
    }

    public void setStartSubmitTime(Date startSubmitTime) {
        this.startSubmitTime = startSubmitTime;
    }

    public Date getEndSubmitTime() {
        return endSubmitTime;
    }

    public void setEndSubmitTime(Date endSubmitTime) {
        this.endSubmitTime = endSubmitTime;
    }

    public Integer getChangeSiteReasonTypeCode() {
        return changeSiteReasonTypeCode;
    }

    public void setChangeSiteReasonTypeCode(Integer changeSiteReasonTypeCode) {
        this.changeSiteReasonTypeCode = changeSiteReasonTypeCode;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public Integer getOffSet() {
        if (pageNumber == null || pageSize == null) {
            return 0;
        }
        return (pageNumber - 1) * pageSize;
    }

    public void setOffSet(Integer offSet) {
        if (pageNumber == null || pageSize == null) {
            this.offSet = 0;
        }else {
            this.offSet = (pageNumber - 1) * pageSize;
        }
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getFirstCheckStatus() {
        return firstCheckStatus;
    }

    public void setFirstCheckStatus(Integer firstCheckStatus) {
        this.firstCheckStatus = firstCheckStatus;
    }

    public Integer getSecondCheckStatus() {
        return secondCheckStatus;
    }

    public void setSecondCheckStatus(Integer secondCheckStatus) {
        this.secondCheckStatus = secondCheckStatus;
    }

    public Integer getCheckEndFlag() {
        return checkEndFlag;
    }

    public void setCheckEndFlag(Integer checkEndFlag) {
        this.checkEndFlag = checkEndFlag;
    }
}