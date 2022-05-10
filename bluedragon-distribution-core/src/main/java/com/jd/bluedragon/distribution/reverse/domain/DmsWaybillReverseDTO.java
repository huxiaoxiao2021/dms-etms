package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.jd.ldop.center.api.receive.dto.DetailReverseReasonDTO;
import com.jd.ldop.center.api.update.dto.WaybillAddress;

public class DmsWaybillReverseDTO implements Serializable {
	private static final long serialVersionUID = -3587967083163404062L;
	private Integer source;
	private Integer reverseType;
	private String waybillCode;
	private Integer orgId;
	private Integer sortCenterId;
	private Integer siteId;
	private String customerCode;
	private Integer packageCount;
	private Integer returnType;
	private String reverseReason;
	private Date operateTime;
	private String operateUser;
	private Integer operateUserId;
	private String remark;
	private List<DmsDetailReverseReasonDTO> detailReverseReasonDTOList;
	private DmsWaybillAddress waybillAddress;
	private Integer chargeType;
	private Integer attributionToJD;
	private BigDecimal weight;
	private BigDecimal volume;
	private Boolean limitReverseFlag;
	private Integer allowReverseCount;

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getReverseType() {
		return this.reverseType;
	}

	public void setReverseType(Integer reverseType) {
		this.reverseType = reverseType;
	}

	public String getWaybillCode() {
		return this.waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getOrgId() {
		return this.orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getSortCenterId() {
		return this.sortCenterId;
	}

	public void setSortCenterId(Integer sortCenterId) {
		this.sortCenterId = sortCenterId;
	}

	public Integer getSiteId() {
		return this.siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getCustomerCode() {
		return this.customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public Integer getPackageCount() {
		return this.packageCount;
	}

	public void setPackageCount(Integer packageCount) {
		this.packageCount = packageCount;
	}

	public Integer getReturnType() {
		return this.returnType;
	}

	public void setReturnType(Integer returnType) {
		this.returnType = returnType;
	}

	public String getReverseReason() {
		return this.reverseReason;
	}

	public void setReverseReason(String reverseReason) {
		this.reverseReason = reverseReason;
	}

	public Date getOperateTime() {
		return this.operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperateUser() {
		return this.operateUser;
	}

	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	public Integer getOperateUserId() {
		return this.operateUserId;
	}

	public void setOperateUserId(Integer operateUserId) {
		this.operateUserId = operateUserId;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<DmsDetailReverseReasonDTO> getDetailReverseReasonDTOList() {
		return this.detailReverseReasonDTOList;
	}

	public void setDetailReverseReasonDTOList(List<DmsDetailReverseReasonDTO> detailReverseReasonDTOList) {
		this.detailReverseReasonDTOList = detailReverseReasonDTOList;
	}

	public DmsWaybillAddress getWaybillAddress() {
		return this.waybillAddress;
	}

	public void setWaybillAddress(DmsWaybillAddress waybillAddress) {
		this.waybillAddress = waybillAddress;
	}

	public Integer getChargeType() {
		return this.chargeType;
	}

	public void setChargeType(Integer chargeType) {
		this.chargeType = chargeType;
	}

	public Integer getAttributionToJD() {
		return this.attributionToJD;
	}

	public void setAttributionToJD(Integer attributionToJD) {
		this.attributionToJD = attributionToJD;
	}

	public BigDecimal getWeight() {
		return this.weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getVolume() {
		return this.volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public Boolean getLimitReverseFlag() {
		return this.limitReverseFlag;
	}

	public void setLimitReverseFlag(Boolean limitReverseFlag) {
		this.limitReverseFlag = limitReverseFlag;
	}

	public Integer getAllowReverseCount() {
		return this.allowReverseCount;
	}

	public void setAllowReverseCount(Integer allowReverseCount) {
		this.allowReverseCount = allowReverseCount;
	}
}