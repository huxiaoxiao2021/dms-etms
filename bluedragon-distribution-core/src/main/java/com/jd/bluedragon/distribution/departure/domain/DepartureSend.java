package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;
import java.util.Date;
/**
 * 发车批次
 * @author libin
 *
 */
public class DepartureSend implements Serializable {
	private static final long serialVersionUID = -8971753291667877699L;
	/** 全局唯一ID */
	private Long departureSendId;
	/** 封签ID */
	private Long shieldsCarId;
	/** 发货交接单号-发货批次号 */
	private String sendCode;
	/** 创建人单位编码 */
	private Integer createSiteCode;

	/** 创建人 */
	private String createUser;

	/** 创建人编码 */
	private Integer createUserCode;

	/** 创建时间 */
	private Date createTime;

	/** 修改时间 */
	private Date updateTime;

	/** 是否删除 '0' 删除 '1' 使用 */
	private Integer yn;
	/**
	 * 三方运单号
	 */
	private String thirdWaybillCode;
	
	/** 运力编码 */
	private String capacityCode;

	public Long getShieldsCarId() {
		return shieldsCarId;
	}

	public void setShieldsCarId(Long shieldsCarId) {
		this.shieldsCarId = shieldsCarId;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
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
		return createTime != null ? (Date) createTime.clone() : null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime != null ? (Date) createTime.clone() : null;
	}

	public Date getUpdateTime() {
		return updateTime != null ? (Date) updateTime.clone() : null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime != null ? (Date) updateTime.clone() : null;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Long getDepartureSendId() {
		return departureSendId;
	}

	public void setDepartureSendId(Long departureSendId) {
		this.departureSendId = departureSendId;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public String getCapacityCode() {
		return capacityCode;
	}

	public void setCapacityCode(String capacityCode) {
		this.capacityCode = capacityCode;
	}

}
