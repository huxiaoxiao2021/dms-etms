package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;

/**
 * @ClassName SendVehicleToScanPackage
 * @Description
 * @Author chenyaguo
 * @Date 2022/3/31 23:04
 **/
public class JySendVehicleToScanPackage implements Serializable {

    private static final long serialVersionUID = -161979898323473867L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 产品类型
     */
    private String productType;


    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }


}
