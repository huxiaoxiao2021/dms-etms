package com.jd.bluedragon.distribution.reverse.domain;

import java.util.Date;

public class PickWare implements java.io.Serializable {

	private static final long serialVersionUID = -5392803771896641567L;

	private Long pickwareId;
	private Integer orgId;
	private String packageCode;
	private String pickwareCode;
	private Long orderId;
	private String operateTime;
	private String operator;
	private Integer canReceive;
	private Integer operateType;
	private String boxCode;
	private Date pickwareTime;
	private Integer cky2;
	private Integer storeId;
	/** 信息指纹 */
	private String fingerprint;

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public Date getPickwareTime() {
		return pickwareTime;
	}

	public void setPickwareTime(Date pickwareTime) {
		this.pickwareTime = pickwareTime;
	}

	public Long getPickwareId() {
		return pickwareId;
	}

	public void setPickwareId(Long pickwareId) {
		this.pickwareId = pickwareId;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getPickwareCode() {
		return pickwareCode;
	}

	public void setPickwareCode(String pickwareCode) {
		this.pickwareCode = pickwareCode;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Integer getCanReceive() {
		return canReceive;
	}

	public void setCanReceive(Integer canReceive) {
		this.canReceive = canReceive;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
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
	
	public String toString() {
		return "PickWare [orgId=" + this.orgId + ", packageCode="
				+ this.packageCode + ", pickwareCode=" + this.pickwareCode
				+ ",orderId=" + this.orderId + ",operateTime="
				+ this.operateTime + ",operator=" + this.operator
				+ ",canReceive=" + this.canReceive + ",operateType="
				+ this.operateType + ",boxCode=" + this.boxCode
				+ ",fingerprint=" + this.fingerprint + "]";
	}


}
