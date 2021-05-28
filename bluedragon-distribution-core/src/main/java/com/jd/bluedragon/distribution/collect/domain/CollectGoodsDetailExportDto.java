package com.jd.bluedragon.distribution.collect.domain;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/12 16:47
 */
public class CollectGoodsDetailExportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 集货位编号 */
    private String collectGoodsPlaceCode;

    /** 集货区编码 */
    private String collectGoodsAreaCode;

    /** 集货位类型 */
    private String collectGoodsPlaceType;

    /** 集货位状态 0-空闲 1-非空闲 */
    private Integer collectGoodsPlaceStatus;

    /** 包裹号 */
    private String packageCode;
    /** 运单号 */
    private String waybillCode;


    /** 包裹总数 */
    private Integer packageCount;

    /** 所属站点编码 */
    private Integer createSiteCode;

    /** 所属站点名称 */
    private String createSiteName;

    /** 创建用户 */
    private String createUser;

    /** 修改用户 */
    private String updateUser;

    private Integer scanPackageCount;

    /**
     * 创建时间
     */
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCollectGoodsPlaceType() {
        return collectGoodsPlaceType;
    }

    public void setCollectGoodsPlaceType(String collectGoodsPlaceType) {
        this.collectGoodsPlaceType = collectGoodsPlaceType;
    }

    public String getCollectGoodsPlaceCode() {
        return collectGoodsPlaceCode;
    }

    public void setCollectGoodsPlaceCode(String collectGoodsPlaceCode) {
        this.collectGoodsPlaceCode = collectGoodsPlaceCode;
    }

    public String getCollectGoodsAreaCode() {
        return collectGoodsAreaCode;
    }

    public void setCollectGoodsAreaCode(String collectGoodsAreaCode) {
        this.collectGoodsAreaCode = collectGoodsAreaCode;
    }



    public Integer getCollectGoodsPlaceStatus() {
        return collectGoodsPlaceStatus;
    }

    public void setCollectGoodsPlaceStatus(Integer collectGoodsPlaceStatus) {
        this.collectGoodsPlaceStatus = collectGoodsPlaceStatus;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getScanPackageCount() {
        return scanPackageCount;
    }

    public void setScanPackageCount(Integer scanPackageCount) {
        this.scanPackageCount = scanPackageCount;
    }
}
    
