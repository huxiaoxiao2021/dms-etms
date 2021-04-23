package com.jd.bluedragon.distribution.product.domain;

import java.math.BigDecimal;

import com.google.common.base.Objects;

public class Product {

	public static final String PRODUCT_ACCESSORY_PREFIX = "附件：";
	public static final String DEFAULT_PRODUCT_ACCESSORY_ID = "-1";
	public static final Integer DEFAULT_PRODUCT_ACCESSORY_QUANTITY = -1;

	private String productId;
	private String name;
	private Integer quantity;
	private BigDecimal price;
	private long skuId;
	private Long profitChannelId;

	public Long getProfitChannelId() {
		return profitChannelId;
	}

	public void setProfitChannelId(Long profitChannelId) {
		this.profitChannelId = profitChannelId;
	}

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

	public long getSkuId() {
		return skuId;
	}

	public void setSkuId(long skuId) {
		this.skuId = skuId;
	}

	public String toString() {
		return Objects.toStringHelper(this).addValue(this.productId).addValue(this.name).addValue(this.price)
				.addValue(this.quantity).addValue(this.skuId).toString();
	}

}
