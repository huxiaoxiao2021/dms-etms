package com.jd.bluedragon.distribution.api.response;

import java.math.BigDecimal;

import com.jd.bluedragon.distribution.api.JdObject;

public class Product extends JdObject {

	private static final long serialVersionUID = -753617634257394668L;
	
	private String productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal productPrice;

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

}
