package com.jd.bluedragon.distribution.abnormal.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 *
 * @ClassName: DmsOperateHint
 * @Description: 运单操作提示-实体类
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
public class DmsOperateHint extends DbEntity {

	private static final long serialVersionUID = 1L;
	/**
	 * 提示语类型  1-用户创建
	 */
	public static final Integer HINT_TYPE_USER = 1;
	/**
	 * 提示语类型  2-系统创建
	 */
	public static final Integer HINT_TYPE_SYS = 2;
	/**
	 * 编码-需要包裹补打（101）
	 */
	public static final Integer HINT_CODE_NEED_REPRINT = 101;
	/**
	 * 名称-需要包裹补打（包裹补打）
	 */
	public static final String HINT_NAME_NEED_REPRINT = "包裹补打";

	 /** 分拣中心编号 */
	private Integer dmsSiteCode;

	 /** 分拣中心名称 */
	private String dmsSiteName;

	 /** 运单号 */
	private String waybillCode;

	 /** 提示语类型  1-用户创建  2-系统创建 */
	private Integer hintType;

	 /** 提示语编码 */
	private Integer hintCode;

	 /** 提示语名称 */
	private String hintName;

	 /** 提示语信息 */
	private String hintMessage;

	 /** 提示语内容 */
	private String hintContent;

	 /** 启用状态，默认为1启用 */
	private Integer isEnable;

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
	 * 显示提示的分拣中心编码
	 */
	private Integer hintDmsCode;

	/**
	 * 显示提示的分拣中心名称
	 */
	private String hintDmsName;

	/**
	 * 显示提示的操作环节
	 */
	private String hintOperateNode;


	/**
	 * 显示提示的时间
	 */
	private Date hintTime;

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
	 * The set method for hintCode.
	 * @param hintCode
	 */
	public void setHintCode(Integer hintCode) {
		this.hintCode = hintCode;
	}

	/**
	 * The get method for hintCode.
	 * @return this.hintCode
	 */
	public Integer getHintCode() {
		return this.hintCode;
	}

	/**
	 * The set method for hintName.
	 * @param hintName
	 */
	public void setHintName(String hintName) {
		this.hintName = hintName;
	}

	/**
	 * The get method for hintName.
	 * @return this.hintName
	 */
	public String getHintName() {
		return this.hintName;
	}

	/**
	 * The set method for hintMessage.
	 * @param hintMessage
	 */
	public void setHintMessage(String hintMessage) {
		this.hintMessage = hintMessage;
	}

	/**
	 * The get method for hintMessage.
	 * @return this.hintMessage
	 */
	public String getHintMessage() {
		return this.hintMessage;
	}

	/**
	 * The set method for isEnable.
	 * @param isEnable
	 */
	public void setIsEnable(Integer isEnable) {
		this.isEnable = isEnable;
	}

	/**
	 * The get method for isEnable.
	 * @return this.isEnable
	 */
	public Integer getIsEnable() {
		return this.isEnable;
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

	/**
	 * @return the hintType
	 */
	public Integer getHintType() {
		return hintType;
	}

	/**
	 * @param hintType the hintType to set
	 */
	public void setHintType(Integer hintType) {
		this.hintType = hintType;
	}

	/**
	 * @return the hintContent
	 */
	public String getHintContent() {
		return hintContent;
	}

	/**
	 * @param hintContent the hintContent to set
	 */
	public void setHintContent(String hintContent) {
		this.hintContent = hintContent;
	}


	public Integer getHintDmsCode() {
		return hintDmsCode;
	}

	public void setHintDmsCode(Integer hintDmsCode) {
		this.hintDmsCode = hintDmsCode;
	}

	public String getHintDmsName() {
		return hintDmsName;
	}

	public void setHintDmsName(String hintDmsName) {
		this.hintDmsName = hintDmsName;
	}

	public String getHintOperateNode() {
		return hintOperateNode;
	}

	public void setHintOperateNode(String hintOperateNode) {
		this.hintOperateNode = hintOperateNode;
	}

	public Date getHintTime() {
		return hintTime;
	}

	public void setHintTime(Date hintTime) {
		this.hintTime = hintTime;
	}

}
