package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;


public class GantryVelocityResponse extends JdResponse {

	private static final long serialVersionUID = 1213634666096239241L;

	/**
   	*龙门架序列号,
    */
	private String gantrySerialNumber;

	/**
	 *
	 * */
	private Integer velocity;

	public String getGantrySerialNumber() {
		return gantrySerialNumber;
	}

	public void setGantrySerialNumber(String gantrySerialNumber) {
		this.gantrySerialNumber = gantrySerialNumber;
	}

	public Integer getVelocity() {
		return velocity;
	}

	public void setVelocity(Integer velocity) {
		this.velocity = velocity;
	}
}
