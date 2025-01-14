package com.jd.bluedragon.distribution.inventory.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryExceptionCondition extends BasePagerCondition {

    /**
     * 机构编号
     */
    private Integer orgId;

    /**
     * 始发分拣中心id
     */
    private Integer createSiteCode;

    /**
     * 任务创建人erp
     */
    private String createUserErp;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 异常类型1：多货 2：少货
     */
    private Integer expType;

    /**
     * 异常状态0:未处理 1:已处理
     */
    private Integer expStatus;

    /**
     * 盘点开始时间
     */
    private String createStartTime;

    /**
     * 盘点结束时间
     */
    private String createEndTime;

    /**
     * 是否筛选重复单
     */
    private Integer isRepeat;
    
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

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getExpType() {
        return expType;
    }

    public void setExpType(Integer expType) {
        this.expType = expType;
    }

    public Integer getExpStatus() {
        return expStatus;
    }

    public void setExpStatus(Integer expStatus) {
        this.expStatus = expStatus;
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

    public Integer getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(Integer isRepeat) {
        this.isRepeat = isRepeat;
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
