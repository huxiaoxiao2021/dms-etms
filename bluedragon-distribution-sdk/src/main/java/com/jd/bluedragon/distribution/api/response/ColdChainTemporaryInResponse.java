package com.jd.bluedragon.distribution.api.response;

/**
 * 冷链暂存入库返回对象
 */
public class ColdChainTemporaryInResponse {

    /**
     * 运单数
     */
    private int waybillCount;

    /**
     * 包裹数
     */
    private int packageCount;

    public int getWaybillCount() {
        return waybillCount;
    }

    public void setWaybillCount(int waybillCount) {
        this.waybillCount = waybillCount;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }
}
