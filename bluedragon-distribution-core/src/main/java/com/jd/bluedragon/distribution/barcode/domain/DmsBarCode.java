package com.jd.bluedragon.distribution.barcode.domain;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年07月12日 16时:15分
 */
public class DmsBarCode {
    private String barcode;//69码
    private String skuId;
    private String productName;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
