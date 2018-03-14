package com.jd.bluedragon.distribution.base.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: DmsStorageArea
 * @Description: 分拣中心库位-实体类
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
public class DmsStorageArea extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 库位类型 1-流向库位 */
	private Integer storageType;

	 /** 库位号 */
	private String storageCode;

	 /** 分拣中心编号 */
	private Integer dmsSiteCode;

	 /** 分拣中心名称 */
	private String dmsSiteName;

	 /** 目的省编号 */
	private Integer desProvinceCode;

	 /** 目的省名称 */
	private String desProvinceName;

	 /** 目的城市编号 */
	private Integer desCityCode;

	 /** 目的城市名称 */
	private String desCityName;

	 /** 创建人code */
	private Integer createUserCode;

	 /** 创建人ERP */
	private String createUser;

	 /** 创建人名称 */
	private String createUserName;

	 /** 更新人code */
	private Integer updateUserCode;

	 /** 更新人ERP */
	private String updateUser;

	 /** 更新人名称 */
	private String updateUserName;

	/**
	 * The set method for storageType.
	 * @param storageType
	 */
	public void setStorageType(Integer storageType) {
		this.storageType = storageType;
	}

	/**
	 * The get method for storageType.
	 * @return this.storageType
	 */
	public Integer getStorageType() {
		return this.storageType;
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
	 * The set method for dmsSiteCode.
	 * @param dmsSiteCode
	 */
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}

	/**
	 * The get method for dmsSiteCode.
	 * @return this.dmsSiteCode
	 */
	public Integer getDmsSiteCode() {
		return this.dmsSiteCode;
	}

	/**
	 * The set method for dmsSiteName.
	 * @param dmsSiteName
	 */
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}

	/**
	 * The get method for dmsSiteName.
	 * @return this.dmsSiteName
	 */
	public String getDmsSiteName() {
		return this.dmsSiteName;
	}

	/**
	 * The set method for desProvinceCode.
	 * @param desProvinceCode
	 */
	public void setDesProvinceCode(Integer desProvinceCode) {
		this.desProvinceCode = desProvinceCode;
	}

	/**
	 * The get method for desProvinceCode.
	 * @return this.desProvinceCode
	 */
	public Integer getDesProvinceCode() {
		return this.desProvinceCode;
	}

	/**
	 * The set method for desProvinceName.
	 * @param desProvinceName
	 */
	public void setDesProvinceName(String desProvinceName) {
		this.desProvinceName = desProvinceName;
	}

	/**
	 * The get method for desProvinceName.
	 * @return this.desProvinceName
	 */
	public String getDesProvinceName() {
		return this.desProvinceName;
	}

	/**
	 * The set method for desCityCode.
	 * @param desCityCode
	 */
	public void setDesCityCode(Integer desCityCode) {
		this.desCityCode = desCityCode;
	}

	/**
	 * The get method for desCityCode.
	 * @return this.desCityCode
	 */
	public Integer getDesCityCode() {
		return this.desCityCode;
	}

	/**
	 * The set method for desCityName.
	 * @param desCityName
	 */
	public void setDesCityName(String desCityName) {
		this.desCityName = desCityName;
	}

	/**
	 * The get method for desCityName.
	 * @return this.desCityName
	 */
	public String getDesCityName() {
		return this.desCityName;
	}

	/**
	 * The set method for createUserCode.
	 * @param createUserCode
	 */
	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	/**
	 * The get method for createUserCode.
	 * @return this.createUserCode
	 */
	public Integer getCreateUserCode() {
		return this.createUserCode;
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
	 * The set method for createUserName.
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 * The get method for createUserName.
	 * @return this.createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 * The set method for updateUserCode.
	 * @param updateUserCode
	 */
	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	/**
	 * The get method for updateUserCode.
	 * @return this.updateUserCode
	 */
	public Integer getUpdateUserCode() {
		return this.updateUserCode;
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


}
