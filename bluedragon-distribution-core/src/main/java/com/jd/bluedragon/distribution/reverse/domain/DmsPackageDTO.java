package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;

public class DmsPackageDTO implements Serializable {
	private static final long serialVersionUID = 8991065453614966323L;
	
	private Integer customerId;
	private String deliveryId;
	private String packageCode;
	private Double weight;
	private Double volumn;
	private Double vloumLong;
	private Double vloumWidth;
	private Double vloumHeight;

	public Integer getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getDeliveryId() {
		return this.deliveryId;
	}

	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getPackageCode() {
		return this.packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public Double getWeight() {
		return this.weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolumn() {
		return this.volumn;
	}

	public void setVolumn(Double volumn) {
		this.volumn = volumn;
	}

	public Double getVloumLong() {
		return this.vloumLong;
	}

	public void setVloumLong(Double vloumLong) {
		this.vloumLong = vloumLong;
	}

	public Double getVloumWidth() {
		return this.vloumWidth;
	}

	public void setVloumWidth(Double vloumWidth) {
		this.vloumWidth = vloumWidth;
	}

	public Double getVloumHeight() {
		return this.vloumHeight;
	}

	public void setVloumHeight(Double vloumHeight) {
		this.vloumHeight = vloumHeight;
	}
}