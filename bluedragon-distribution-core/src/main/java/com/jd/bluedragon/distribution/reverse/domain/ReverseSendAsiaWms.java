package com.jd.bluedragon.distribution.reverse.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "ReturnRequest")
public class ReverseSendAsiaWms {

	private String sendCode;

	private String operateTime;

	private String userName;

	private String orderId;

	private String packageCodes;

	private int lossQuantity;
	
	private List<Product> proList;

	private Integer cky2;

	private Integer storeId;

	private Integer orgId;

	private Integer type = -1;
	
	private Integer isInStore;
	
	/**
	 * 本批次订单总数
	 */
	private Integer orderSum;
	
	/**
	 * 本批次包裹总数
	 */
	private Integer packSum;
	
	/**
	 * 逆向编码，标识是用户退货，还是返仓再生产
	 * 10 表示需要再次生产
	 * 20 表示真正退货
	 * @return
	 */
	private Integer reverseCode;
	
	/**
	 * 逆向原因
	 * @return
	 */
	private String reverseReason;
	
	public Integer getIsInStore() {
		return isInStore;
	}

	public void setIsInStore(Integer isInStore) {
		this.isInStore = isInStore;
	}

	@XmlTransient
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCky2() {
		return cky2;
	}

	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

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

	@XmlElementWrapper(name = "productRequests")
	@XmlElement(name = "ProductRequest")
	public List<Product> getProList() {
		return proList;
	}

	public void setProList(List<Product> proList) {
		this.proList = proList;
	}

	public Integer getOrderSum() {
		return orderSum;
	}

	public void setOrderSum(Integer orderSum) {
		this.orderSum = orderSum;
	}

	public Integer getPackSum() {
		return packSum;
	}

	public void setPackSum(Integer packSum) {
		this.packSum = packSum;
	}
	@XmlTransient
	public Integer getReverseCode() {
		return reverseCode;
	}

	public void setReverseCode(Integer reverseCode) {
		this.reverseCode = reverseCode;
	}
	@XmlTransient
	public String getReverseReason() {
		return reverseReason;
	}

	public void setReverseReason(String reverseReason) {
		this.reverseReason = reverseReason;
	}

}
