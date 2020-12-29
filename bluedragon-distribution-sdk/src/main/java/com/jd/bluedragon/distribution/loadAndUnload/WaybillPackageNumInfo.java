package com.jd.bluedragon.distribution.loadAndUnload;

public class WaybillPackageNumInfo {

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 运单应卸包裹数量
     */
    private int forceAmount;

    /**
     * 运单总包裹数量
     */
    private int packageAmount;


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public int getForceAmount() {
        return forceAmount;
    }

    public void setForceAmount(int forceAmount) {
        this.forceAmount = forceAmount;
    }

    public int getPackageAmount() {
        return packageAmount;
    }

    public void setPackageAmount(int packageAmount) {
        this.packageAmount = packageAmount;
    }
}
