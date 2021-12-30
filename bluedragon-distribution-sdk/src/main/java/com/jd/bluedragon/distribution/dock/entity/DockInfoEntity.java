package com.jd.bluedragon.distribution.dock.entity;

import com.jd.bluedragon.distribution.dock.enums.DockAttributeEnums;
import com.jd.bluedragon.distribution.dock.enums.DockTypeEnums;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.dock.entity
 * @ClassName: DockInfo
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2021/11/29 13:53
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class DockInfoEntity {

    private Long id;

    /**
     * 月台编号
     */
    private String dockCode;

    /**
     * 站点编码
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 机构号
     */
    private Integer orgId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 月台类型：枚举包括：长途、短途、传站、摆渡。默认为空
     */
    private DockTypeEnums dockType;

    /**
     * 月台属性：枚举包括：装、卸、默认为空。
     */
    private DockAttributeEnums dockAttribute;

    /**
     * 可操作车型：可多选
     */
    private List<AllowedVehicleEntity> allowedVehicleTypes;

    /**
     * 是否有升降平台
     */
    private Integer isHasDockLeveller;

    /**
     * 是否带秤
     */
    private Integer isHasScales;

    /**
     * 是否即装即卸
     */
    private Integer isImmediately;

    /**
     * 月台高度  m³
     */
    private Double height;

    private String createUserName;

    private Date createTime;

    private String updateUserName;

    private Date updateTime;

    private Byte isDelete;

    private Date ts;

    public Long getId() {
        return id;
    }

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

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public DockTypeEnums getDockType() {
        return dockType;
    }

    public void setDockType(DockTypeEnums dockType) {
        this.dockType = dockType;
    }

    public DockAttributeEnums getDockAttribute() {
        return dockAttribute;
    }

    public void setDockAttribute(DockAttributeEnums dockAttribute) {
        this.dockAttribute = dockAttribute;
    }

    public List<AllowedVehicleEntity> getAllowedVehicleTypes() {
        return allowedVehicleTypes;
    }

    public void setAllowedVehicleTypes(List<AllowedVehicleEntity> allowedVehicleTypes) {
        this.allowedVehicleTypes = allowedVehicleTypes;
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

    public Integer getIsImmediately() {
        return isImmediately;
    }

    public void setIsImmediately(Integer isImmediately) {
        this.isImmediately = isImmediately;
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
}
