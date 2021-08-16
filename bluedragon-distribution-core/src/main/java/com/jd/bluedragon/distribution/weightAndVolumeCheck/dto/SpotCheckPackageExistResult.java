package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

import java.io.Serializable;

/**
 * 已抽检数据结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-11 12:12:14 周三
 */
public class SpotCheckPackageExistResult implements Serializable {

    /**
     * 运单纬度数据是否存在
     */
    private boolean waybillSpotCheckExist;

    /**
     * 已抽检包裹总数
     */
    private long packageSpotCheckTotal;

    public SpotCheckPackageExistResult() {
    }

    public SpotCheckPackageExistResult(boolean waybillSpotCheckExist, long packageSpotCheckTotal) {
        this.waybillSpotCheckExist = waybillSpotCheckExist;
        this.packageSpotCheckTotal = packageSpotCheckTotal;
    }

    public boolean getWaybillSpotCheckExist() {
        return waybillSpotCheckExist;
    }

    public SpotCheckPackageExistResult setWaybillSpotCheckExist(boolean waybillSpotCheckExist) {
        this.waybillSpotCheckExist = waybillSpotCheckExist;
        return this;
    }

    public long getPackageSpotCheckTotal() {
        return packageSpotCheckTotal;
    }

    public SpotCheckPackageExistResult setPackageSpotCheckTotal(long packageSpotCheckTotal) {
        this.packageSpotCheckTotal = packageSpotCheckTotal;
        return this;
    }
}
