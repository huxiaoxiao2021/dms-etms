package com.jd.bluedragon.distribution.order.domain;

public class OrderBankRequest {

	private String orderId;
	private String appId;
	private String appToken;
	
	/** 用户pin */
	private String pin;

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public OrderBankRequest() {
	}

	public OrderBankRequest(String orderId, String appId, String appToken) {
		this.orderId = orderId;
		this.appId = appId;
		this.appToken = appToken;
	}
	
	public OrderBankRequest(String orderId,String pin, String appId, String appToken) {
		this.orderId = orderId;
		this.pin = pin;
		this.appId = appId;
		this.appToken = appToken;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

}
