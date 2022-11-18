package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;

/**
 * @ClassName SendVehicleProductTypeAgg
 * @Description 发车待扫产品类型及待扫数量统计
 * @Author chenyaguo
 * @Date 2022/4/1 15:12
 **/
public class SendVehicleProductTypeAgg implements Serializable {

    private static final long serialVersionUID = -3804083429789576112L;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品类型名称
     */
    private String productTypeName;

    /**
     * 待扫数量
     */
    private Long count;

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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
