package com.jd.bluedragon.distribution.half.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;

/**
 *
 * @ClassName: PackageHalfCondition
 * @Description: 包裹半收操作-查询条件
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
public class PackageHalfCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 运单号 */
	private String waybillCode;

	 /** 半收类型（1-包裹半收，2-明细半收） */
	private Integer halfType;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;


	/** 操作站点编号 */
	private Long operateSiteCode;

	/** 操作站点名称 */
	private String operateSiteName;

	private Date createTimeGE;
	private Date createTimeLE;

	/**
	 * The set method for waybillCode.
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * The get method for waybillCode.
	 * @return this.waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 * The set method for halfType.
	 * @param halfType
	 */
	public void setHalfType(Integer halfType) {
		this.halfType = halfType;
	}

	/**
	 * The get method for halfType.
	 * @return this.halfType
	 */
	public Integer getHalfType() {
		return this.halfType;
	}

	/**
	 * The set method for createUser.
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * The get method for createUser.
	 * @return this.createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 * The set method for updateUser.
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * The get method for updateUser.
	 * @return this.updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}

	public Long getOperateSiteCode() {
		return operateSiteCode;
	}

	public void setOperateSiteCode(Long operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}

	public String getOperateSiteName() {
		return operateSiteName;
	}

	public void setOperateSiteName(String operateSiteName) {
		this.operateSiteName = operateSiteName;
	}

	public Date getCreateTimeGE() {
		return createTimeGE;
	}

	public void setCreateTimeGE(Date createTimeGE) {
		createTimeGE.setHours(0);
		createTimeGE.setMinutes(0);
		createTimeGE.setSeconds(0);
		this.createTimeGE = createTimeGE;
	}

	public Date getCreateTimeLE() {
		return createTimeLE;
	}

	public void setCreateTimeLE(Date createTimeLE) {
		createTimeLE.setHours(23);
		createTimeLE.setMinutes(59);
		createTimeLE.setSeconds(59);
		this.createTimeLE = createTimeLE;

	}
}
