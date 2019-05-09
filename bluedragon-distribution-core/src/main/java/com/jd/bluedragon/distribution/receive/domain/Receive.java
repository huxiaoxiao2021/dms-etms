package com.jd.bluedragon.distribution.receive.domain;

import java.io.Serializable;
import java.util.Date;

public class Receive implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long receiveId;

	private String createUser;

	private Integer createUserCode;

	private Date createTime;

	private Integer createSiteCode;

	private String waybillCode;

	private String packageBarcode;

	private String boxCode;

	private Short receiveType;

	private String carCode;

	private String shieldsCarCode;
	
	private Date shieldsCarTime;

	private Short boxingType;

	private Short status;

	private Date updateTime;

	private Integer yn;

	private Integer topNumber;
	
	/* 创建站点name */
	private String createSiteName;

	private String turnoverBoxCode;
	
	/** 封箱号 */
	private String sealBoxCode;
	
	 /**排队号*/
    private String queueNo;

    /*没有调用的地方*/
    @Deprecated
    private String departureCarId;

	public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public String getSealBoxCode() {
		return this.sealBoxCode;
	}

	public void setSealBoxCode(String sealBoxCode) {
		this.sealBoxCode = sealBoxCode;
	}
	
	public String getTurnoverBoxCode() {
		return this.turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

	public String getCreateSiteName() {
		return this.createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Integer getCreateUserCode() {
		return this.createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Integer getCreateSiteCode() {
		return this.createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getTopNumber() {
		return this.topNumber;
	}

	public void setTopNumber(Integer topNumber) {
		this.topNumber = topNumber;
	}

	public String getCarCode() {
		return this.carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public String getShieldsCarCode() {
		return this.shieldsCarCode;
	}

	public void setShieldsCarCode(String shieldsCarCode) {
		this.shieldsCarCode = shieldsCarCode;
	}

	public Short getReceiveType() {
		return this.receiveType;
	}

	public void setReceiveType(Short receiveType) {
		this.receiveType = receiveType;
	}

	public Short getBoxingType() {
		return this.boxingType;
	}

	public void setBoxingType(Short boxingType) {
		this.boxingType = boxingType;
	}

	public Long getReceiveId() {
		return this.receiveId;
	}

	public void setReceiveId(Long receiveId) {
		this.receiveId = receiveId;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return this.createTime != null ? (Date) this.createTime.clone() : null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime != null ? (Date) createTime.clone() : null;
	}

	public String getWaybillCode() {
		return this.waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageBarcode() {
		return this.packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getBoxCode() {
		return this.boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return this.updateTime != null ? (Date) this.updateTime.clone() : null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime != null ? (Date) updateTime.clone() : null;
	}

	public Date getShieldsCarTime() {
		return this.shieldsCarTime != null ? (Date) this.shieldsCarTime.clone() : null;
	}

	public void setShieldsCarTime(Date shieldsCarTime) {
		this.shieldsCarTime = shieldsCarTime != null ? (Date) shieldsCarTime.clone() : null;
	}

	public Integer getYn() {
		return this.yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

    public String getDepartureCarId() {
        return departureCarId;
    }

    public void setDepartureCarId(String departureCarId) {
        this.departureCarId = departureCarId;
    }
}