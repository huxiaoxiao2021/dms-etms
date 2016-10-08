package com.jd.bluedragon.distribution.fastRefund.domain;

import java.util.Date;

public class FastRefund {
	String OrderId;//订单号　
	 
	String applyReason;//申请原因
	 
	String ReqErp;//申请人erp账号
	 
	String ReqName;//申请人name
	 
	Integer systemId = 13;//分拣中心 13
	 
	Long applyDate;//申请时间
	 
	Integer reqDMSId;//分拣中心id
	 
	String ReqDMSName;//分拣中心名称
	/**	
	1、 京东自营订单 
	2、 SOP订单 
	3、 纯外单 
	4、 售后绑定的面单
	*/
	Integer waybillSign;
	
	String sys;//拦截mq添加  作为标示

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderid) {
		OrderId = orderid;
	}

	public String getApplyReason() {
		return applyReason;
	}

	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}

	public String getReqErp() {
		return ReqErp;
	}

	public void setReqErp(String reqErp) {
		ReqErp = reqErp;
	}

	public String getReqName() {
		return ReqName;
	}

	public void setReqName(String reqName) {
		ReqName = reqName;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Long getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Long applyDate) {
		this.applyDate = applyDate;
	}

	public Integer getReqDMSId() {
		return reqDMSId;
	}

	public void setReqDMSId(Integer reqDMSId) {
		this.reqDMSId = reqDMSId;
	}

	public String getReqDMSName() {
		return ReqDMSName;
	}

	public void setReqDMSName(String reqDMSName) {
		ReqDMSName = reqDMSName;
	}

	public Integer getWaybillSign() {
		return waybillSign;
	}

	public void setWaybillSign(Integer waybillSign) {
		this.waybillSign = waybillSign;
	}

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}

}
