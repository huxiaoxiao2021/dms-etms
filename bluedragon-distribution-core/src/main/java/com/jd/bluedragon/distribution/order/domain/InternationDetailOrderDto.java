package com.jd.bluedragon.distribution.order.domain;

import java.math.BigDecimal;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/15 15:31
 */
public class InternationDetailOrderDto {

    private  String name;
    private Integer num;
    private  Long productId;
    private BigDecimal price;
    private Long skuId;
    private Integer  profitChannelId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getProfitChannelId() {
        return profitChannelId;
    }

    public void setProfitChannelId(Integer profitChannelId) {
        this.profitChannelId = profitChannelId;
    }
}
    
