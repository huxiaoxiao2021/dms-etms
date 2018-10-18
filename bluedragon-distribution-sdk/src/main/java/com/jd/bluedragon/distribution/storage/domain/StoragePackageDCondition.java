package com.jd.bluedragon.distribution.storage.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: StoragePackageDCondition
 * @Description: 储位包裹明细表-查询条件
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
public class StoragePackageDCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 履约单号 */
	private String performanceCode;

	 /** 储位号 */
	private String storageCode;

	 /** 运单号 */
	private String waybillCode;

	 /** 包裹号 */
	private String packageCode;

	 /** 所属分拣中心 */
	private Long createSiteCode;

	 /** 发货时间 */
	private Date sendTime;

	 /** 创建人 */
	private String createUser;

	 /** 更新人 */
	private String updateUser;

	/**
	 * The set method for performanceCode.
	 * @param performanceCode
	 */
	public void setPerformanceCode(String performanceCode) {
		this.performanceCode = performanceCode;
	}

	/**
	 * The get method for performanceCode.
	 * @return this.performanceCode
	 */
	public String getPerformanceCode() {
		return this.performanceCode;
	}

	/**
	 * The set method for storageCode.
	 * @param storageCode
	 */
	public void setStorageCode(String storageCode) {
		this.storageCode = storageCode;
	}

	/**
	 * The get method for storageCode.
	 * @return this.storageCode
	 */
	public String getStorageCode() {
		return this.storageCode;
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
	 * The set method for createSiteCode.
	 * @param createSiteCode
	 */
	public void setCreateSiteCode(Long createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * The get method for createSiteCode.
	 * @return this.createSiteCode
	 */
	public Long getCreateSiteCode() {
		return this.createSiteCode;
	}

	/**
	 * The set method for sendTime.
	 * @param sendTime
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * The get method for sendTime.
	 * @return this.sendTime
	 */
	public Date getSendTime() {
		return this.sendTime;
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
