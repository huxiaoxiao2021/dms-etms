package com.jd.bluedragon.distribution.weightVolume.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/12/21
 * @Description:
 */
public class ZeroWeightVolumeCheckEntity implements Serializable {

    static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 运单标识
     */
    private String waybillSign;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 商家编码
     */
    private String customerCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
