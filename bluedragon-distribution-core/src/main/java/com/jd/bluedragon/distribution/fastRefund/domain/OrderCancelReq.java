package com.jd.bluedragon.distribution.fastRefund.domain;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "OrderCancelReq")
public class OrderCancelReq {

	String orderId;/*订单号*/
	Integer storeId;/*库房号*/
	Integer cky2;/*配送中心*/
	String userPin = "";/*客户帐号*/
	Integer needDeductScore = 2;/*是否需要扣除积分（1 需要扣除 2 不需要扣除）*/
	Integer isdisplay = 1;/*是否在客户的取消列表中显示（1 显示 2 不显示）*/
	Integer orgid;/*订单机构编号*/
	String reqPerson = "DMS";/*取消申请人帐号*/
	String reqPersonName = "DMS";/*取消申请人姓名*/
	Integer ordercancelReason = 308;/*默认308（客户取消订单）*//*取消原因 int 类型（参考如下取消原因列表）*/
	
	public OrderCancelReq(){
		
	}
	
	public OrderCancelReq(String orderId,Integer storeId,Integer cky2,Integer orgid){
		this.orderId = orderId;
		this.storeId = storeId;
		this.cky2 = cky2;
		this.orgid = orgid;
	}
	
	public String getUserPin() {
		return userPin;
	}
	public Integer getNeedDeductScore() {
		return needDeductScore;
	}
	public Integer getIsdisplay() {
		return isdisplay;
	}
	public String getReqPerson() {
		return reqPerson;
	}
	public String getReqPersonName() {
		return reqPersonName;
	}
	public Integer getOrdercancelReason() {
		return ordercancelReason;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public Integer getCky2() {
		return cky2;
	}
	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}
	public Integer getOrgid() {
		return orgid;
	}
	public void setOrgid(Integer orgid) {
		this.orgid = orgid;
	}

	public void setUserPin(String userPin) {
		this.userPin = userPin;
	}

	public void setNeedDeductScore(Integer needDeductScore) {
		this.needDeductScore = needDeductScore;
	}

	public void setIsdisplay(Integer isdisplay) {
		this.isdisplay = isdisplay;
	}

	public void setReqPerson(String reqPerson) {
		this.reqPerson = reqPerson;
	}

	public void setReqPersonName(String reqPersonName) {
		this.reqPersonName = reqPersonName;
	}

	public void setOrdercancelReason(Integer ordercancelReason) {
		this.ordercancelReason = ordercancelReason;
	}

	
}
