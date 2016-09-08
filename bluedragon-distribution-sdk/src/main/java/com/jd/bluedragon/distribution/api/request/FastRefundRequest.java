package com.jd.bluedragon.distribution.api.request;

import java.util.Date;

import com.jd.bluedragon.distribution.api.JdRequest;

public class FastRefundRequest  extends JdRequest{

	String waybillCode;//订单号　

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
}
