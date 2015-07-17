package com.jd.bluedragon.distribution.partnerWaybill.domain;

import java.util.Date;

public class PartnerWaybill implements java.io.Serializable{

	private static final long serialVersionUID = -6572496574513844955L;

	/*运单包裹关联表主键*/
	private Long relationId;

	/* 三方运单号 */
	private String partnerWaybillCode;

	/* 京东运单号 */
	private String waybillCode;
	
	/* 包裹号 */
	private String packageBarcode;
	
	/*0 待处理  1 处理完成  3 异常*/
	private Integer status;

	/*三方code*/
	private Integer partnerSiteCode;
	
	/*创建人name*/
	private String createUser;

	/*创建人code*/
	private Integer createUserCode;

	/*创建时间*/
	private Date createTime;

	/*操作站点code*/
	private Integer createSiteCode;

	/*最后操作人name*/
	private String updateUser;
	
	/*最后操作人code*/
	private Integer updateUserCode;

	/*最后操作时间*/
	private Date updateTime;
	
	private Integer yn;

	protected Integer startNo;
    protected Integer limitNo;
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((packageBarcode == null) ? 0 : packageBarcode.hashCode());
		result = prime * result
				+ ((partnerSiteCode == null) ? 0 : partnerSiteCode.hashCode());
		result = prime
				* result
				+ ((partnerWaybillCode == null) ? 0 : partnerWaybillCode
						.hashCode());
		result = prime * result
				+ ((waybillCode == null) ? 0 : waybillCode.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PartnerWaybill other = (PartnerWaybill) obj;
		if (packageBarcode == null) {
			if (other.packageBarcode != null)
				return false;
		} else if (!packageBarcode.equals(other.packageBarcode))
			return false;
		if (partnerSiteCode == null) {
			if (other.partnerSiteCode != null)
				return false;
		} else if (!partnerSiteCode.equals(other.partnerSiteCode))
			return false;
		if (partnerWaybillCode == null) {
			if (other.partnerWaybillCode != null)
				return false;
		} else if (!partnerWaybillCode.equals(other.partnerWaybillCode))
			return false;
		if (waybillCode == null) {
			if (other.waybillCode != null)
				return false;
		} else if (!waybillCode.equals(other.waybillCode))
			return false;
		return true;
	}
	
	public Long getRelationId() {
		return relationId;
	}
	public void setRelationId(Long relationId) {
		this.relationId = relationId;
	}
	public String getPartnerWaybillCode() {
		return partnerWaybillCode;
	}
	public void setPartnerWaybillCode(String partnerWaybillCode) {
		this.partnerWaybillCode = partnerWaybillCode;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getPartnerSiteCode() {
		return partnerSiteCode;
	}
	public void setPartnerSiteCode(Integer partnerSiteCode) {
		this.partnerSiteCode = partnerSiteCode;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Integer getCreateUserCode() {
		return createUserCode;
	}
	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}
	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Integer getUpdateUserCode() {
		return updateUserCode;
	}
	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}
	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public Integer getStartNo() {
		return startNo;
	}
	public void setStartNo(Integer startNo) {
		this.startNo = startNo;
	}
	public Integer getLimitNo() {
		return limitNo;
	}
	public void setLimitNo(Integer limitNo) {
		this.limitNo = limitNo;
	}
	
}
