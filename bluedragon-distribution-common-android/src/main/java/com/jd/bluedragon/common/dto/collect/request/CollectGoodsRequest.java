package com.jd.bluedragon.common.dto.collect.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

public class CollectGoodsRequest implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 用户
     */
    private User user;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;


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

    /**
     * 转移 目的集货位
     */
    private String targetCollectGoodsPlaceCode;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
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

    public String getTargetCollectGoodsPlaceCode() {
        return targetCollectGoodsPlaceCode;
    }

    public void setTargetCollectGoodsPlaceCode(String targetCollectGoodsPlaceCode) {
        this.targetCollectGoodsPlaceCode = targetCollectGoodsPlaceCode;
    }
}
