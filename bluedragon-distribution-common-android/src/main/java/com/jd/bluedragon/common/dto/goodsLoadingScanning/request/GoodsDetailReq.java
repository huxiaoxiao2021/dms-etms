package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

public class GoodsDetailReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 包裹号集合（运单下面总包裹数，默认10个包裹的包裹号）
     */
    private String packageCodes;

    /**
     * 任务号
     */
    private Long taskId;

    /**
     * 运单号
     */
    private String wayBillCode;

    /**
     * 总包裹数量
     */
    private Integer packageAmount;

    /**
     * 库存数量
     */
    private Integer goodsAmount;

    /**
     * 已装车数量
     */
    private Integer loadAmount;

    /**
     * 未装车数量
     */
    private Integer unloadAmount;

    /**
     * 强制下发数量
     */
    private Integer forceAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(String packageCodes) {
        this.packageCodes = packageCodes;
    }

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

    public Integer getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(Integer packageAmount) {
        this.packageAmount = packageAmount;
    }

    public Integer getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(Integer goodsAmount) {
        this.goodsAmount = goodsAmount;
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

    public Integer getForceAmount() {
        return forceAmount;
    }

    public void setForceAmount(Integer forceAmount) {
        this.forceAmount = forceAmount;
    }
}
