package com.jd.bluedragon.distribution.sealVehicle.domain;


import java.io.Serializable;
import java.util.List;

public class UnSealVehicleDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	* 运力编码
	* */
	private String transportCode;

	 /*
	 * 始发站点编号
	 * */
	private Integer createSiteCode;

	/*
	* 批次相关信息
	* */
	private List<SealVehicleSendCodeInfo> sendCodeInfoList;

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public List<SealVehicleSendCodeInfo> getSendCodeInfoList() {
		return sendCodeInfoList;
	}

	public void setSendCodeInfoList(List<SealVehicleSendCodeInfo> sendCodeInfoList) {
		this.sendCodeInfoList = sendCodeInfoList;
	}
}
