package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;

public class WaybillCodeSummatyDto implements Serializable{

	private static final long serialVersionUID = 2351274550728235935L;

	/**  运单号     */
	private String waybillCode;
	
	/**  包裹数     */
	private Integer packagebarNum;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getPackagebarNum() {
		return packagebarNum;
	}

	public void setPackagebarNum(Integer packagebarNum) {
		this.packagebarNum = packagebarNum;
	}
	
}
