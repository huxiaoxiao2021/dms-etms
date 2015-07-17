package com.jd.bluedragon.distribution.reverse.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "ReturnRequest")
public class ReverseSendSpwms {

	private String sendCode;
	
	private String operateTime;

	private String userName;

	private String orderId;
	
	//220738970-2-2-,220738970-1-2-
	private String packageCodes;
	
	//默认为0
	private int lossQuantity;

	
	private List<Product> proList;
	
	private Integer cky2;
	
	private Integer storeId;
	
	private Integer orgId;

	@XmlTransient
	public Integer getCky2() {
		return cky2;
	}

	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}

	@XmlTransient
	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	@XmlTransient
	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPackageCodes() {
		return packageCodes;
	}

	public void setPackageCodes(String packageCodes) {
		this.packageCodes = packageCodes;
	}

	public int getLossQuantity() {
		return lossQuantity;
	}

	public void setLossQuantity(int lossQuantity) {
		this.lossQuantity = lossQuantity;
	}

	@XmlElementWrapper(name="productRequests")  
	@XmlElement(name = "ProductRequest")
	public List<Product> getProList() {
		return proList;
	}

	public void setProList(List<Product> proList) {
		this.proList = proList;
	}

}
