package com.jd.bluedragon.distribution.half.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: PackageHalfDetail
 * @Description: 包裹半收操作明细表-实体类
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
public class PackageHalfDetail extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 运单号 */
	private String waybillCode;

	 /** 半收类型（1-包裹半收，2-明细半收） */
	private Integer halfType;

	 /** 包裹号 */
	private String packageCode;

	 /** 操作站点编号 */
	private Long operateSiteCode;

	 /** 操作站点名称 */
	private String operateSiteName;

	 /** 配送结果（1-妥投，2-拒收，3-丢失） */
	private Integer resultType;

	 /** 拒收原因（1-破损 ,2- 丢失,3- 报废 ,4-客户原因,5- 其他） */
	private Integer reasonType;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;

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
	 * The set method for packageCode.
	 * @param packageCode
	 */
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	/**
	 * The get method for packageCode.
	 * @return this.packageCode
	 */
	public String getPackageCode() {
		return this.packageCode;
	}

	/**
	 * The set method for operateSiteCode.
	 * @param operateSiteCode
	 */
	public void setOperateSiteCode(Long operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}

	/**
	 * The get method for operateSiteCode.
	 * @return this.operateSiteCode
	 */
	public Long getOperateSiteCode() {
		return this.operateSiteCode;
	}

	/**
	 * The set method for operateSiteName.
	 * @param operateSiteName
	 */
	public void setOperateSiteName(String operateSiteName) {
		this.operateSiteName = operateSiteName;
	}

	/**
	 * The get method for operateSiteName.
	 * @return this.operateSiteName
	 */
	public String getOperateSiteName() {
		return this.operateSiteName;
	}

	/**
	 * The set method for resultType.
	 * @param resultType
	 */
	public void setResultType(Integer resultType) {
		this.resultType = resultType;
	}

	/**
	 * The get method for resultType.
	 * @return this.resultType
	 */
	public Integer getResultType() {
		return this.resultType;
	}

	/**
	 * The set method for reasonType.
	 * @param reasonType
	 */
	public void setReasonType(Integer reasonType) {
		this.reasonType = reasonType;
	}

	/**
	 * The get method for reasonType.
	 * @return this.reasonType
	 */
	public Integer getReasonType() {
		return this.reasonType;
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


}
