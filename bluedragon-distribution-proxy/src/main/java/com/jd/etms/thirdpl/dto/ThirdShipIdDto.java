package com.jd.etms.thirdpl.dto;

import java.io.Serializable;
import java.util.List;

public class ThirdShipIdDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer thirdId;
	private String thirdName;
	private List<String> shipIds;
	private Integer flagOrderType;//（1、pop商家发往分拣中心运单号2、干线运输运单号）
	public Integer getThirdId() {
		return thirdId;
	}
	public void setThirdId(Integer thirdId) {
		this.thirdId = thirdId;
	}
	public String getThirdName() {
		return thirdName;
	}
	public void setThirdName(String thirdName) {
		this.thirdName = thirdName;
	}
	public List<String> getShipIds() {
		return shipIds;
	}
	public void setShipIds(List<String> shipIds) {
		this.shipIds = shipIds;
	}
	public Integer getFlagOrderType() {
		return flagOrderType;
	}
	public void setFlagOrderType(Integer flagOrderType) {
		this.flagOrderType = flagOrderType;
	}
	
}
