package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;

public class StationMatchRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 地址
	 */
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
