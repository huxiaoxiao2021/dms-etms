package com.jd.bluedragon.distribution.newseal.domain;

public class PreSealMeasureInfoRequest {

	 /** 运力编码 */
	private String transportCode;

    /** 车牌号List */
    private String vehicleNumber;

	/** 重量 */
	private Double weight;

	/** 体积 */
	private Double volume;

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
}
