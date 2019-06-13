package com.jd.bluedragon.distribution.inventory.domain;

import java.io.Serializable;
import java.util.List;

public class InventoryWaybillInfo implements Serializable, Comparable<InventoryWaybillInfo> {

    /*
    * 运单号
    * */
    private String waybillCode;

    /*
     * 待盘包裹总数
     * */
    private Integer needInventoryCount;

    /*
     * 当前已盘数
     * */
    private Integer scannedCount;

    /*
     * 当前未盘包裹数
     * */
    private Integer noInventoryCount;

    /*
     * 操作时间，取当前运单下最新扫描包裹的时间
     * */
    private Long operateTime;

    public InventoryWaybillInfo() {};

    public InventoryWaybillInfo(String waybillCode) {
        this.waybillCode = waybillCode;
        this.needInventoryCount = 0;
        this.scannedCount = 0;
        this.noInventoryCount = 0;
    }

    public InventoryWaybillInfo(String waybillCode, Long operateTime) {
        this.waybillCode = waybillCode;
        this.operateTime = operateTime;
        this.needInventoryCount = 0;
        this.scannedCount = 0;
        this.noInventoryCount = 0;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getNeedInventoryCount() {
        return needInventoryCount;
    }

    public void setNeedInventoryCount(Integer needInventoryCount) {
        this.needInventoryCount = needInventoryCount;
    }

    public Integer getScannedCount() {
        return scannedCount;
    }

    public void setScannedCount(Integer scannedCount) {
        this.scannedCount = scannedCount;
    }

    public Integer getNoInventoryCount() {
        return noInventoryCount;
    }

    public void setNoInventoryCount(Integer noInventoryCount) {
        this.noInventoryCount = noInventoryCount;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public void setNeedInventoryCountBaseCurr(Integer count) {
        this.needInventoryCount += count;
    }

    public void setScannedCountBaseCurr(Integer count) {
        this.scannedCount += count;
    }

    public void setNoInventoryCountBaseCurr(Integer count) {
        this.noInventoryCount += count;
    }

    public void setOperateTimeNew(Long time) {
        if (this.operateTime == null || time > this.operateTime) {
            this.operateTime = time;
        }
    }

    @Override
    public int compareTo(InventoryWaybillInfo inventoryWaybillInfo) {
        if(this.operateTime > inventoryWaybillInfo.getOperateTime()){
            return 1;
        }
        else if(this.operateTime < inventoryWaybillInfo.getOperateTime()){
            return -1;
        }
        else{
            return 0;
        }
    }
}
