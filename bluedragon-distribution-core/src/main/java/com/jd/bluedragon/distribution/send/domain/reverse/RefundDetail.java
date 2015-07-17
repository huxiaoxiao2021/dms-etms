package com.jd.bluedragon.distribution.send.domain.reverse;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "refundDetail")
public class RefundDetail implements Serializable {
	
	private static final long serialVersionUID = 6564346798185264279L;
	
	private int id;
	
	private String orderId;
	private String refundType;
	private String refundReason;
	private String refundMessage;
	private Date operateTime;
	private String operator;
	private Date createTime;
	private Integer caseId;
	private String rsn;
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getOrderId() {
		return this.orderId;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getRefundType() {
		return this.refundType;
	}
	
	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}
	
	public String getRefundReason() {
		return this.refundReason;
	}
	
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	
	public String getRefundMessage() {
		return this.refundMessage;
	}
	
	public void setRefundMessage(String refundMessage) {
		this.refundMessage = refundMessage;
	}
	
	public Date getCreateTime() {
		return this.createTime == null ? null : (Date) this.createTime.clone();
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime == null ? null : (Date) createTime.clone();
	}
	
	public Integer getCaseId() {
		return this.caseId;
	}
	
	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}
	
	public Date getOperateTime() {
		return this.operateTime == null ? null : (Date) this.operateTime.clone();
	}
	
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime == null ? null : (Date) operateTime.clone();
	}
	
	public String getOperator() {
		return this.operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public String getRsn() {
		return this.rsn;
	}
	
	public void setRsn(String rsn) {
		this.rsn = rsn;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		
		if (!this.getClass().equals(object.getClass())) {
			return false;
		}
		
		RefundDetail refundDetail = (RefundDetail) object;
		if (this.orderId == null) {
			return this.orderId == refundDetail.orderId;
		}
		
		return this.orderId.equals(refundDetail.orderId);
	}
	
	@Override
	public int hashCode() {
		return this.orderId.hashCode();
	}
}
