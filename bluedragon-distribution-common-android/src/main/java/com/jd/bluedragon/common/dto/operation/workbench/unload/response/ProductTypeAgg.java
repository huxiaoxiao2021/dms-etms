package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.select.StringSelectOption;

import java.io.Serializable;

/**
 * @ClassName ProductTypeAgg
 * @Description 按产品类型统计待扫包裹数
 * @Author wyh
 * @Date 2022/4/1 15:12
 **/
public class ProductTypeAgg implements Serializable {

    private static final long serialVersionUID = -3804083429789576617L;

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

    /**
     * 排序
     */
    private Integer order;


    public static class OrderComparator implements java.util.Comparator<ProductTypeAgg> {
        @Override
        public int compare(ProductTypeAgg o1, ProductTypeAgg o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
