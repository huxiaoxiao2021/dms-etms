package com.jd.bluedragon.distribution.saf.domain;

public class WaybillRequest implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 504383881249827782L;
	/**
	 * 箱号
	 * */
	private String boxCode;
	

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

}
