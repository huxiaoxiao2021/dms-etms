package com.jd.bluedragon.distribution.third.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: ThirdBoxDetailCondition
 * @Description: 三方装箱明细表-查询条件
 * @author wuyoude
 * @date 2020年01月07日 16:34:04
 *
 */
public class ThirdBoxDetailCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 租户编码 */
	private String tenantCode;

	 /** 始发站点ID */
	private Long startSiteId;

	 /** 始发站点编码 */
	private String startSiteCode;

	 /** 目的站点ID */
	private Long endSiteId;

	 /** 目的站点编码 */
	private String endSiteCode;

	 /** 操作人id	 */
	private String operatorId;

	 /** 操作人name */
	private String operatorName;

	 /** 操作单位名称 */
	private String operatorUnitName;

	 /** 操作时间 */
	private Date operatorTime;

	 /** 箱号 */
	private String boxCode;

	 /** 包裹号 */
	private String packageCode;

	 /** 运单号 */
	private String waybillCode;

	 /** 更新人id */
	private String updateUserId;

	 /** 更新人name */
	private String updateUserName;

	 /** 更新人操作单位名称 */
	private String updateUnitName;

	/**
	 * The set method for tenantCode.
	 * @param tenantCode
	 */
	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	/**
	 * The get method for tenantCode.
	 * @return this.tenantCode
	 */
	public String getTenantCode() {
		return this.tenantCode;
	}

	/**
	 * The set method for startSiteId.
	 * @param startSiteId
	 */
	public void setStartSiteId(Long startSiteId) {
		this.startSiteId = startSiteId;
	}

	/**
	 * The get method for startSiteId.
	 * @return this.startSiteId
	 */
	public Long getStartSiteId() {
		return this.startSiteId;
	}

	/**
	 * The set method for startSiteCode.
	 * @param startSiteCode
	 */
	public void setStartSiteCode(String startSiteCode) {
		this.startSiteCode = startSiteCode;
	}

	/**
	 * The get method for startSiteCode.
	 * @return this.startSiteCode
	 */
	public String getStartSiteCode() {
		return this.startSiteCode;
	}

	/**
	 * The set method for endSiteId.
	 * @param endSiteId
	 */
	public void setEndSiteId(Long endSiteId) {
		this.endSiteId = endSiteId;
	}

	/**
	 * The get method for endSiteId.
	 * @return this.endSiteId
	 */
	public Long getEndSiteId() {
		return this.endSiteId;
	}

	/**
	 * The set method for endSiteCode.
	 * @param endSiteCode
	 */
	public void setEndSiteCode(String endSiteCode) {
		this.endSiteCode = endSiteCode;
	}

	/**
	 * The get method for endSiteCode.
	 * @return this.endSiteCode
	 */
	public String getEndSiteCode() {
		return this.endSiteCode;
	}

	/**
	 * The set method for operatorId.
	 * @param operatorId
	 */
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	/**
	 * The get method for operatorId.
	 * @return this.operatorId
	 */
	public String getOperatorId() {
		return this.operatorId;
	}

	/**
	 * The set method for operatorName.
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * The get method for operatorName.
	 * @return this.operatorName
	 */
	public String getOperatorName() {
		return this.operatorName;
	}

	/**
	 * The set method for operatorUnitName.
	 * @param operatorUnitName
	 */
	public void setOperatorUnitName(String operatorUnitName) {
		this.operatorUnitName = operatorUnitName;
	}

	/**
	 * The get method for operatorUnitName.
	 * @return this.operatorUnitName
	 */
	public String getOperatorUnitName() {
		return this.operatorUnitName;
	}

	/**
	 * The set method for operatorTime.
	 * @param operatorTime
	 */
	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
	}

	/**
	 * The get method for operatorTime.
	 * @return this.operatorTime
	 */
	public Date getOperatorTime() {
		return this.operatorTime;
	}

	/**
	 * The set method for boxCode.
	 * @param boxCode
	 */
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	/**
	 * The get method for boxCode.
	 * @return this.boxCode
	 */
	public String getBoxCode() {
		return this.boxCode;
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
	 * The set method for updateUserId.
	 * @param updateUserId
	 */
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	/**
	 * The get method for updateUserId.
	 * @return this.updateUserId
	 */
	public String getUpdateUserId() {
		return this.updateUserId;
	}

	/**
	 * The set method for updateUserName.
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 * The get method for updateUserName.
	 * @return this.updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 * The set method for updateUnitName.
	 * @param updateUnitName
	 */
	public void setUpdateUnitName(String updateUnitName) {
		this.updateUnitName = updateUnitName;
	}

	/**
	 * The get method for updateUnitName.
	 * @return this.updateUnitName
	 */
	public String getUpdateUnitName() {
		return this.updateUnitName;
	}


}
