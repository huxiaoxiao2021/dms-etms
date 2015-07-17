package com.jd.bluedragon.distribution.reverse.domain;

import javax.xml.bind.annotation.XmlRootElement;


//逆向收货接收对象
@XmlRootElement(name = "ReceiveRequest")
public class ReceiveRequest {


	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	//是否可收
    private String canReceive;
	/** 收货时间 */
    private String operateTime;
    /** 操作人编号 */
    private String operatorId;
	private String orderId;
	//机构ID
    private String orgId;
    /** 包裹编号 */
    private String packageCode;
	/** 取件单号 */
	private String pickWareCode;

	//收货类型
    private String receiveType;
	//拒收编码
    private String rejectCode;
	//拒收原因
    private String rejectMessage;
	/** 发货批次 */
    private String sendCode;
	private String storeId;
    /** 操作人code */
    private String userCode;
    /** 操作人 */
    private String userName;
    public String getCanReceive() {
		return canReceive;
	}
    
    public String getOperateTime() {
		return operateTime;
	}
    
    public String getOperatorId() {
		return operatorId;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getOrgId() {
		return orgId;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public String getPickWareCode() {
		return pickWareCode;
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

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public void setPickWareCode(String pickWareCode) {
		this.pickWareCode = pickWareCode;
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
