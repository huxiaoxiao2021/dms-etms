package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ProductTypeAgg;

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

    /**
     * 排序
     */
    private Integer order;

    public static class OrderComparator implements java.util.Comparator<SendVehicleProductTypeAgg> {
        @Override
        public int compare(SendVehicleProductTypeAgg o1, SendVehicleProductTypeAgg o2) {
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
