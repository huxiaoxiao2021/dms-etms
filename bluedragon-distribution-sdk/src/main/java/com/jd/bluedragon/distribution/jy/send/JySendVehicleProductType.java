package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/4 17:15
 * @Description:
 */
public class JySendVehicleProductType implements Serializable {

    private static final long serialVersionUID = 7437837375891856971L;

    //产品类型
    private String productType;
    //产品类型名称
    private String productTypeName;
    //待扫数量
    private Long productwaitScanCount;

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

    public Long getProductwaitScanCount() {
        return productwaitScanCount;
    }

    public void setProductwaitScanCount(Long productwaitScanCount) {
        this.productwaitScanCount = productwaitScanCount;
    }
}
