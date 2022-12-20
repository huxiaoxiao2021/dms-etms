package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendVehicleToScanPackageDetailResponse
 * @Description
 * @Author chenyaguo
 * @Date 2022/3/31 21:59
 **/
public class SendVehicleToScanPackageDetailDto implements Serializable {

    private static final long serialVersionUID = -823689398329722274L;

    /**
     * 总数
     */
    private Long packageCount;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品类型名称
     */
    private String productTypeName;

    /**
     * 待扫包裹明细
     */
    private List<JySendVehicleToScanPackage> packageCodeList;

    public Long getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Long packageCount) {
        this.packageCount = packageCount;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public List<JySendVehicleToScanPackage> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<JySendVehicleToScanPackage> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }
}
