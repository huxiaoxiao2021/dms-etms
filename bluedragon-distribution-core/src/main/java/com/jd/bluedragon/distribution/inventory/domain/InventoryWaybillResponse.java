package com.jd.bluedragon.distribution.inventory.domain;

import java.io.Serializable;
import java.util.List;

public class InventoryWaybillResponse implements Serializable {

    /*
     * 待盘总运单数
     * */
    private Integer waybillSum;

    /*
     * 带盘总包裹数
     * */
    private Integer packageSum;

    /*
     * 当前已扫包裹数
     * */
    private Integer scannedSum;

    /*
    * 运单信息列表
    * */
    private List<InventoryWaybillInfo> inventoryInfoList;

    public Integer getWaybillSum() {
        return waybillSum;
    }

    public void setWaybillSum(Integer waybillSum) {
        this.waybillSum = waybillSum;
    }

    public Integer getPackageSum() {
        return packageSum;
    }

    public void setPackageSum(Integer packageSum) {
        this.packageSum = packageSum;
    }

    public Integer getScannedSum() {
        return scannedSum;
    }

    public void setScannedSum(Integer scannedSum) {
        this.scannedSum = scannedSum;
    }

    public List<InventoryWaybillInfo> getInventoryInfoList() {
        return inventoryInfoList;
    }

    public void setInventoryInfoList(List<InventoryWaybillInfo> inventoryInfoList) {
        this.inventoryInfoList = inventoryInfoList;
    }
}
