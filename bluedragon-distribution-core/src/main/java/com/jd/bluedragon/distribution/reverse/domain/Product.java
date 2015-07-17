package com.jd.bluedragon.distribution.reverse.domain;


public class Product {
	private String productId;
	
	private Integer productNum;
	
	private String productName;
	private String productPrice;
	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductLoss() {
		return productLoss;
	}

	public void setProductLoss(String productLoss) {
		this.productLoss = productLoss;
	}

	private String productLoss;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Integer getProductNum() {
		return productNum;
	}

	public void setProductNum(Integer productNum) {
		this.productNum = productNum;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}