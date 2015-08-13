package com.jd.bluedragon.distribution.globaltrade.domain;

import java.io.Serializable;
import java.util.Date;

public class GlobalTrade implements Serializable{

	private Long id;
	private String waybillCode;
	private String packageBarcode;
	private String orderCode;
	private Integer dmsCode;
	private String dmsName;
	private Date sendTime;
	private String sendCode;
	private String carCode;
	private Integer approvalCode;
	private Date approvalTime;
	private Date createTime;
	private Date updateTime;
	private String remark;
	private Integer yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public Integer getDmsCode() {
		return dmsCode;
	}

	public void setDmsCode(Integer dmsCode) {
		this.dmsCode = dmsCode;
	}

	public String getDmsName() {
		return dmsName;
	}

	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public Integer getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(Integer approvalCode) {
		this.approvalCode = approvalCode;
	}

	public Date getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

}
