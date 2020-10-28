package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;

public class GoodsDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;


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
     * 运单颜色状态--0无特殊颜色,1绿色,2橙色,3黄色,4红色
     */
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
