package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.util.List;

public class SealVehicleTransportVehicleDto {

	private static final long serialVersionUID = 1L;

	 /*
	 * 运力编码
	 * */
	private String transportCode;

	/*
	* 车牌号List
	* */
	private List<String> vehicleNumberList;

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public List<String> getVehicleNumberList() {
		return vehicleNumberList;
	}

	public void setVehicleNumberList(List<String> vehicleNumberList) {
		this.vehicleNumberList = vehicleNumberList;
	}
}
