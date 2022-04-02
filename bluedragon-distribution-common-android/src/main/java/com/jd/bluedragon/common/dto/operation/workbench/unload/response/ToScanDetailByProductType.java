package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UnloadScanAggByProductType
 * @Description 按产品类型统计待扫包裹
 * @Author wyh
 * @Date 2022/3/31 21:59
 **/
public class ToScanDetailByProductType implements Serializable {

    private static final long serialVersionUID = -823689398329770274L;

    /**
     * 总数
     */
    private Long barCodeCount;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品类型名称
     */
    private String productTypeName;

    /**
     * 待扫明细
     */
    private List<UnloadScanBarCode> barCodeList;

    public Long getBarCodeCount() {
        return barCodeCount;
    }

    public void setBarCodeCount(Long barCodeCount) {
        this.barCodeCount = barCodeCount;
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

    public List<UnloadScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<UnloadScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
