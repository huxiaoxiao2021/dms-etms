package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;

/**
 * @ClassName UnloadScanAggByProductType
 * @Description 按产品类型统计货物总量
 * @Author wyh
 * @Date 2022/3/31 21:59
 **/
public class UnloadScanAggByProductType implements Serializable {

    private static final long serialVersionUID = -823689398329770274L;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品类型名称
     */
    private String productTypeName;

    /**
     * 应扫数量
     */
    private Long shouldScanCount;

    /**
     * 已扫数量
     */
    private Long actualScanCount;

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

    public Long getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Long shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Long getActualScanCount() {
        return actualScanCount;
    }

    public void setActualScanCount(Long actualScanCount) {
        this.actualScanCount = actualScanCount;
    }
}
