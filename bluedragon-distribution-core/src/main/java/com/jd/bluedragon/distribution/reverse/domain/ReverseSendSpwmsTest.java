package com.jd.bluedragon.distribution.reverse.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReceiveRequest")
public class ReverseSendSpwmsTest {

	private String canReceive;
	private String operateTime;
	
	
	private String orderId;
	private String orgId;
	private String receiveType;
	private String rejectCode;
	private String rejectMessage;
	private String sendCode;
	private String storeId;
	private String userCode;
	private String userName;
	public String getCanReceive() {
		return canReceive;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public String getOrderId() {
		return orderId;
	}
	public String getOrgId() {
		return orgId;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public String getRejectCode() {
		return rejectCode;
	}
	public String getRejectMessage() {
		return rejectMessage;
	}
	public String getSendCode() {
		return sendCode;
	}
	public String getStoreId() {
		return storeId;
	}
	public String getUserCode() {
		return userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setCanReceive(String canReceive) {
		this.canReceive = canReceive;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public void setRejectCode(String rejectCode) {
		this.rejectCode = rejectCode;
	}
	public void setRejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

} 	