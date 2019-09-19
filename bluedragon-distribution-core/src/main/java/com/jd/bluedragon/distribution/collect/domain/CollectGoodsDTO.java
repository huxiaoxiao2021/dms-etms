package com.jd.bluedragon.distribution.collect.domain;

import java.io.Serializable;

public class CollectGoodsDTO implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 集货位编号 */
    private String collectGoodsPlaceCode;

    /** 集货区编码 */
    private String collectGoodsAreaCode;

    /** 集货位类型 */
    private Integer collectGoodsPlaceType;

    /** 集货位状态 0-空闲 1-非空闲 */
    private Integer collectGoodsPlaceStatus;

    /** 包裹号 */
    private String packageCode;

    /** 包裹总数 */
    private Integer packageCount;

    /** 包裹总数 */
    private Integer scanPackageCount;

    /** 所属站点编码 */
    private Integer operateSiteCode;

    /** 所属站点名称 */
    private String operateSiteName;

    /** 创建用户 */
    private Integer operateUserId;

    /** 创建用户 */
    private String operateUserErp;

    /** 创建用户 */
    private String operateUserName;

    /** 操作时间  毫秒时间戳 */
    private Long operateTime;


    /**
     * 转移 目的集货位
     */
    private String targetCollectGoodsPlaceCode;

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

    public Integer getCollectGoodsPlaceType() {
        return collectGoodsPlaceType;
    }

    public void setCollectGoodsPlaceType(Integer collectGoodsPlaceType) {
        this.collectGoodsPlaceType = collectGoodsPlaceType;
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

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getScanPackageCount() {
        return scanPackageCount;
    }

    public void setScanPackageCount(Integer scanPackageCount) {
        this.scanPackageCount = scanPackageCount;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getTargetCollectGoodsPlaceCode() {
        return targetCollectGoodsPlaceCode;
    }

    public void setTargetCollectGoodsPlaceCode(String targetCollectGoodsPlaceCode) {
        this.targetCollectGoodsPlaceCode = targetCollectGoodsPlaceCode;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }
}
