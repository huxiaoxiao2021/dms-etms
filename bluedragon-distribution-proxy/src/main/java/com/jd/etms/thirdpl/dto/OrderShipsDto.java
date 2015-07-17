package com.jd.etms.thirdpl.dto;

import java.io.Serializable;
import java.util.List;

public class OrderShipsDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String orderId;
	private List<ThirdShipIdDto> thirdShipIdDto;
	private int clearOld;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public List<ThirdShipIdDto> getThirdShipIdDto() {
		return thirdShipIdDto;
	}
	public void setThirdShipIdDto(List<ThirdShipIdDto> thirdShipIdDto) {
		this.thirdShipIdDto = thirdShipIdDto;
	}
	public int getClearOld() {
		return clearOld;
	}
	public void setClearOld(int clearOld) {
		this.clearOld = clearOld;
	}	
}
