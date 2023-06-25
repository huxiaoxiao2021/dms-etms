package com.jd.bluedragon.distribution.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryTaskCondition extends BasePagerCondition {

    private Integer orgId;

    private Integer createSiteCode;

    private Integer directionCode;

    private String createUserErp;

    private String createStartTime;

    private String createEndTime;

    private String completeStartTime;

    private String completeEndTime;

    /**
     * 省区编码
     */
    private String provinceAgencyCode;
    /**
     * 枢纽编码
     */
    private String areaHubCode;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getDirectionCode() {
        return directionCode;
    }

    public void setDirectionCode(Integer directionCode) {
        this.directionCode = directionCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
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

    public String getCompleteStartTime() {
        return completeStartTime;
    }

    public void setCompleteStartTime(String completeStartTime) {
        this.completeStartTime = completeStartTime;
    }

    public String getCompleteEndTime() {
        return completeEndTime;
    }

    public void setCompleteEndTime(String completeEndTime) {
        this.completeEndTime = completeEndTime;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }
}
