package com.jd.bluedragon.distribution.fastRefund.domain;

import java.util.Date;
/**
 * 正向【分拣理货】的逆向订单发送,供快退系统和拦截系统消费合并mq的domain
 * @author lihuachang
 * 
 */
public class FastRefundBlockerComplete {
	
	String orderId;//订单号　
	
	String orderIdOld;//老运单号
	
	String vendorId;//客户订单号
	 
	String applyReason;//申请原因
	 
	String ReqErp;//申请人erp账号
	 
	String ReqName;//申请人name
	 
	Integer systemId;//分拣中心 
	 
	Long applyDate;//申请时间
	 
	Integer reqDMSId;//分拣中心id
	 
	String ReqDMSName;//分拣中心名称
	/**	
	1、 京东自营订单 
	2、 SOP订单 
	3、 纯外单 
	4、 售后绑定的面单
	*/
	String waybillSign;
	
	//拦截系统需要的字段信息
	Integer orderType;
	String messageType;
	String operatTime;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderid) {
		orderId = orderid;
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

	public String getWaybillSign() {
		return waybillSign;
	}

	public void setWaybillSign(String waybillSign) {
		this.waybillSign = waybillSign;
	}

	public String getOrderIdOld() {
		return orderIdOld;
	}

	public void setOrderIdOld(String orderIdOld) {
		this.orderIdOld = orderIdOld;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getOperatTime() {
		return operatTime;
	}

	public void setOperatTime(String operatTime) {
		this.operatTime = operatTime;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	
}
