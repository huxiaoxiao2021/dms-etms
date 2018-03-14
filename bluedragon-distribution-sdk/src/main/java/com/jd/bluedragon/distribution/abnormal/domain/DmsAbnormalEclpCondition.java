package com.jd.bluedragon.distribution.abnormal.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: DmsAbnormalEclpCondition
 * @Description: ECLP外呼申请表-查询条件
 * @author wuyoude
 * @date 2018年03月14日 16:31:20
 *
 */
public class DmsAbnormalEclpCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 分拣中心编号 */
	private Integer dmsSiteCode;

	 /** 分拣中心名称 */
	private String dmsSiteName;

	 /** 运单号 */
	private String waybillCode;

	 /** 待协商类型 */
	private Integer consultType;

	 /** 待协商原因 */
	private String consultReason;

	 /** 待协商原因说明 */
	private String consultMark;

	 /** 客服是否反馈，默认未反馈为0 */
	private Integer isReceipt;

	 /** 客服反馈时间 */
	private Date receiptTime;

	 /** 客服反馈结果类型 */
	private Integer receiptType;

	 /** 客服反馈结果 */
	private String receiptValue;

	 /** 客服反馈备注 */
	private String receiptMark;

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
	 * The set method for consultType.
	 * @param consultType
	 */
	public void setConsultType(Integer consultType) {
		this.consultType = consultType;
	}

	/**
	 * The get method for consultType.
	 * @return this.consultType
	 */
	public Integer getConsultType() {
		return this.consultType;
	}

	/**
	 * The set method for consultReason.
	 * @param consultReason
	 */
	public void setConsultReason(String consultReason) {
		this.consultReason = consultReason;
	}

	/**
	 * The get method for consultReason.
	 * @return this.consultReason
	 */
	public String getConsultReason() {
		return this.consultReason;
	}

	/**
	 * The set method for consultMark.
	 * @param consultMark
	 */
	public void setConsultMark(String consultMark) {
		this.consultMark = consultMark;
	}

	/**
	 * The get method for consultMark.
	 * @return this.consultMark
	 */
	public String getConsultMark() {
		return this.consultMark;
	}

	/**
	 * The set method for isReceipt.
	 * @param isReceipt
	 */
	public void setIsReceipt(Integer isReceipt) {
		this.isReceipt = isReceipt;
	}

	/**
	 * The get method for isReceipt.
	 * @return this.isReceipt
	 */
	public Integer getIsReceipt() {
		return this.isReceipt;
	}

	/**
	 * The set method for receiptTime.
	 * @param receiptTime
	 */
	public void setReceiptTime(Date receiptTime) {
		this.receiptTime = receiptTime;
	}

	/**
	 * The get method for receiptTime.
	 * @return this.receiptTime
	 */
	public Date getReceiptTime() {
		return this.receiptTime;
	}

	/**
	 * The set method for receiptType.
	 * @param receiptType
	 */
	public void setReceiptType(Integer receiptType) {
		this.receiptType = receiptType;
	}

	/**
	 * The get method for receiptType.
	 * @return this.receiptType
	 */
	public Integer getReceiptType() {
		return this.receiptType;
	}

	/**
	 * The set method for receiptValue.
	 * @param receiptValue
	 */
	public void setReceiptValue(String receiptValue) {
		this.receiptValue = receiptValue;
	}

	/**
	 * The get method for receiptValue.
	 * @return this.receiptValue
	 */
	public String getReceiptValue() {
		return this.receiptValue;
	}

	/**
	 * The set method for receiptMark.
	 * @param receiptMark
	 */
	public void setReceiptMark(String receiptMark) {
		this.receiptMark = receiptMark;
	}

	/**
	 * The get method for receiptMark.
	 * @return this.receiptMark
	 */
	public String getReceiptMark() {
		return this.receiptMark;
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
