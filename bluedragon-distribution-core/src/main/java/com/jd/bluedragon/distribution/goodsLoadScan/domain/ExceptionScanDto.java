package com.jd.bluedragon.distribution.goodsLoadScan.domain;

import java.io.Serializable;

public class ExceptionScanDto implements Serializable {
    private static final long serialVersionUID = -7623509285189482980L;

    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 已装车数量
     */
    private Integer loadAmount;

    /**
     * 未装车数量
     */
    private Integer unloadAmount;

    /**
     * 当前操作人
     */
    private String operator;

    /**
     * 当前操作人编码
     */
    private Integer operatorCode;

    /**
     * 当前分拣中心name
     */
    private String currentSiteName;

    /**
     * 当前分拣中心name
     */
    private Integer currentSiteCode;

    /**
     * 是否强制发货
     */
    private Integer forceStatus;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getWayBillCode() {
        return wayBillCode;
    }

    public void setWayBillCode(String wayBillCode) {
        this.wayBillCode = wayBillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(Integer loadAmount) {
        this.loadAmount = loadAmount;
    }

    public Integer getUnloadAmount() {
        return unloadAmount;
    }

    public void setUnloadAmount(Integer unloadAmount) {
        this.unloadAmount = unloadAmount;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getCurrentSiteName() {
        return currentSiteName;
    }

    public void setCurrentSiteName(String currentSiteName) {
        this.currentSiteName = currentSiteName;
    }

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public Integer getForceStatus() {
        return forceStatus;
    }

    public void setForceStatus(Integer forceStatus) {
        this.forceStatus = forceStatus;
    }
}
