package com.jd.bluedragon.distribution.half.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: PackageHalfRedeliveryCondition
 * @Description: 包裹半收协商再投业务表-查询条件
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
public class PackageHalfRedeliveryCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 分拣中心编号 */
	private Integer dmsSiteCode;

	 /** 分拣中心名称 */
	private String dmsSiteName;

	 /** 运单号 */
	private String waybillCode;

	 /** 运单状态 */
	private Integer waybillState;

	 /** 包裹号 */
	private String packageCode;

	 /** 处理状态,默认已反馈（1-已反馈 ,2- 已处理） */
	private Integer dealState;

	 /** 包裹号 */
	private String packageRemark;

	 /** eclp反馈结果，对应MQ包裹状态 */
	private Integer packageState;

	 /** eclp反馈时间，对应MQ操作时间 */
	private Date eclpDealTime;

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

	/** 发起外呼起始时间 */
	private Date startTime;

	/** 发起外呼截止时间 */
	private Date endTime;

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
	 * The set method for waybillState.
	 * @param waybillState
	 */
	public void setWaybillState(Integer waybillState) {
		this.waybillState = waybillState;
	}

	/**
	 * The get method for waybillState.
	 * @return this.waybillState
	 */
	public Integer getWaybillState() {
		return this.waybillState;
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
	 * The set method for dealState.
	 * @param dealState
	 */
	public void setDealState(Integer dealState) {
		this.dealState = dealState;
	}

	/**
	 * The get method for dealState.
	 * @return this.dealState
	 */
	public Integer getDealState() {
		return this.dealState;
	}

	/**
	 * The set method for packageRemark.
	 * @param packageRemark
	 */
	public void setPackageRemark(String packageRemark) {
		this.packageRemark = packageRemark;
	}

	/**
	 * The get method for packageRemark.
	 * @return this.packageRemark
	 */
	public String getPackageRemark() {
		return this.packageRemark;
	}

	/**
	 * The set method for packageState.
	 * @param packageState
	 */
	public void setPackageState(Integer packageState) {
		this.packageState = packageState;
	}

	/**
	 * The get method for packageState.
	 * @return this.packageState
	 */
	public Integer getPackageState() {
		return this.packageState;
	}

	/**
	 * The set method for eclpDealTime.
	 * @param eclpDealTime
	 */
	public void setEclpDealTime(Date eclpDealTime) {
		this.eclpDealTime = eclpDealTime;
	}

	/**
	 * The get method for eclpDealTime.
	 * @return this.eclpDealTime
	 */
	public Date getEclpDealTime() {
		return this.eclpDealTime;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
