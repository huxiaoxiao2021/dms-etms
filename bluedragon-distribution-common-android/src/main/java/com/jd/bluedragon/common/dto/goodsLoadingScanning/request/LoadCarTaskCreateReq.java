package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description:装车任务创建
 * @author: wuming
 * @create: 2020-10-15 16:31
 */
public class LoadCarTaskCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌
     */
    private String licenseNumber;

    /**
     * 目的场地Id
     */
    private Long endSiteCode;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 创建人所属转运中心Id
     */
    private Long currentSiteCode;

    /**
     * 创建人所属转运中心名称
     */
    private String currentSiteName;

    /**
     * 创建人erp
     */
    private String createUserErp;

    /**
     * 创建人姓名
     */
    private String createUserName;

    public LoadCarTaskCreateReq() {
    }


    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Long getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(Long endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Long currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public String getCurrentSiteName() {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName) {
        this.currentSiteName = currentSiteName;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
