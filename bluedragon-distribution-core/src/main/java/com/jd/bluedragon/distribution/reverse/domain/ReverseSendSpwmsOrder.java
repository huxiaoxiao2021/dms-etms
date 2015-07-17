package com.jd.bluedragon.distribution.reverse.domain;

public class ReverseSendSpwmsOrder {
    
    private String productCode;
    private String productId;
	private String productName;
	private Double productPrice;
	private String sendCode;
	private String spareCode;
	private String waybillCode;
	public String getProductCode() {
		return productCode;
	}
	public String getProductId() {
		return productId;
	}
	public String getProductName() {
		return productName;
	}
	public Double getProductPrice() {
		return productPrice;
	}
	public String getSendCode() {
		return sendCode;
	}
	public String getSpareCode() {
		return spareCode;
	}
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
    public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}
    public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}
    public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
}