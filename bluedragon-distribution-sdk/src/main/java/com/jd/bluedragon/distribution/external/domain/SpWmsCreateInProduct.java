package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/17
 * @Description: 备件库小工具 ，商品与备件条码关系
 */
public class SpWmsCreateInProduct implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 备件条码
     */
    private String spareCode;

    /**
     * 商品编码
     */
    private String productCode;

    public String getSpareCode() {
        return spareCode;
    }

    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
