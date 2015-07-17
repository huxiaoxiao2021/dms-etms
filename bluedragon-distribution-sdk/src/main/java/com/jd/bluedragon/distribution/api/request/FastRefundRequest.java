package com.jd.bluedragon.distribution.api.request;

import java.util.Date;

import com.jd.bluedragon.distribution.api.JdRequest;

public class FastRefundRequest  extends JdRequest{

	Long waybillCode;//订单号　

	public Long getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(Long waybillCode) {
		this.waybillCode = waybillCode;
	}
}
