package com.jd.bluedragon.distribution.transport.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: ArSendCode
 * @Description: 发货批次表-实体类
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public class ArSendCode extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 航空单号 */
	private String orderCode;

	 /** 发货批次 */
	private String sendCode;

	 /** 创建用户 */
	private String createUser;

	 /** 修改用户 */
	private String updateUser;

	/**
	 * The set method for orderCode.
	 * @param orderCode
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * The get method for orderCode.
	 * @return this.orderCode
	 */
	public String getOrderCode() {
		return this.orderCode;
	}

	/**
	 * The set method for sendCode.
	 * @param sendCode
	 */
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	/**
	 * The get method for sendCode.
	 * @return this.sendCode
	 */
	public String getSendCode() {
		return this.sendCode;
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
