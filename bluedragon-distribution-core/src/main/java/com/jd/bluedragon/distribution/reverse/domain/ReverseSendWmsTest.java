package com.jd.bluedragon.distribution.reverse.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ReceiveRequest")
public class ReverseSendWmsTest {

	private String sendCode;
	private String orderId;
	private String receiveType;
	private String operateTime;
	private String userName;
	private String userCode;
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	private String canReceive;
	private String rejectCode;
	private String rejectMessage;
	public String getSendCode() {
		return sendCode;
	}
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCanReceive() {
		return canReceive;
	}
	public void setCanReceive(String canReceive) {
		this.canReceive = canReceive;
	}
	public String getRejectCode() {
		return rejectCode;
	}
	public void setRejectCode(String rejectCode) {
		this.rejectCode = rejectCode;
	}
	public String getRejectMessage() {
		return rejectMessage;
	}
	public void setRejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}

} 	