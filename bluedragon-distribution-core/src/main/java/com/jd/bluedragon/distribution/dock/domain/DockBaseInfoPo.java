package com.jd.bluedragon.distribution.dock.domain;

import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.ql.dms.common.web.mvc.api.Entity;

import java.util.Date;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.domain
 * @ClassName: DockBaseInfoPo
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/23 15:03
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class DockBaseInfoPo implements Entity {

    /**
     * 数据库ID
     */
    private Long id;

    /**
     * 月台编号
     */
    private String dockCode;

    /**
     * 站点ID
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 机构ID
     */
    private Integer orgId;

    /**
     * 机柜名称
     */
    private String orgName;

    /**
     * 月台类型：长途、短途、传站、摆渡
     * DockTypeEnums
     */
    private Integer dockType;

    /**
     * 月台属性：装、卸
     * DockAttributeEnums
     */
    private Integer dockAttribute;

    /**
     * 可操作车型
     * 格式： ["0","1","2"]
     * @see BasicQueryWSManager#getVehicleTypeByType(java.lang.String, int)
     */
    private String allowedVehicleType;

    /**
     * 是否即装即卸
     */
    private Integer isImmediately;

    /**
     * 是否包含升降平台
     */
    private Integer isHasDockLeveller;

    /**
     * 是否带秤
     */
    private Integer isHasScales;

    /**
     * 高度 单位：M
     */
    private Double height;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUserName;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 数据库删除标志
     */
    private Byte isDelete;

    /**
     * 数据库时间
     */
    private Date ts;

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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDockCode() {
        return dockCode;
    }

    public void setDockCode(String dockCode) {
        this.dockCode = dockCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getDockType() {
        return dockType;
    }

    public void setDockType(Integer dockType) {
        this.dockType = dockType;
    }

    public Integer getDockAttribute() {
        return dockAttribute;
    }

    public void setDockAttribute(Integer dockAttribute) {
        this.dockAttribute = dockAttribute;
    }

    public String getAllowedVehicleType() {
        return allowedVehicleType;
    }

    public void setAllowedVehicleType(String allowedVehicleType) {
        this.allowedVehicleType = allowedVehicleType;
    }

    public Integer getIsImmediately() {
        return isImmediately;
    }

    public void setIsImmediately(Integer isImmediately) {
        this.isImmediately = isImmediately;
    }

    public Integer getIsHasDockLeveller() {
        return isHasDockLeveller;
    }

    public void setIsHasDockLeveller(Integer isHasDockLeveller) {
        this.isHasDockLeveller = isHasDockLeveller;
    }

    public Integer getIsHasScales() {
        return isHasScales;
    }

    public void setIsHasScales(Integer isHasScales) {
        this.isHasScales = isHasScales;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
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
}
