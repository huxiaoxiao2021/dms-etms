package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.util.Date;

/**
 * 发货批次关系表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendCodeEntity implements Serializable {

	private static final long serialVersionUID = -4917748300842188066L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * send_vehicle业务主键
	 */
	private String sendVehicleBizId;
	/**
	 * send_detail业务主键
	 */
	private String sendDetailBizId;
	/**
	 * 发货批次号
	 */
	private String sendCode;
	/**
	 * 创建人ERP
	 */
	private String createUserErp;
	/**
	 * 创建人姓名
	 */
	private String createUserName;
	/**
	 * 更新人ERP
	 */
	private String updateUserErp;
	/**
	 * 更新人姓名
	 */
	private String updateUserName;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 是否删除：1-有效，0-删除
	 */
	private Integer yn;
	/**
	 * 数据库时间
	 */
	private Date ts;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSendVehicleBizId() {
		return sendVehicleBizId;
	}

	public void setSendVehicleBizId(String sendVehicleBizId) {
		this.sendVehicleBizId = sendVehicleBizId;
	}

	public String getSendDetailBizId() {
		return sendDetailBizId;
	}

	public void setSendDetailBizId(String sendDetailBizId) {
		this.sendDetailBizId = sendDetailBizId;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getCreateUserErp() {
		return createUserErp;
	}

	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public String getUpdateUserErp() {
		return updateUserErp;
	}

	public void setUpdateUserErp(String updateUserErp) {
		this.updateUserErp = updateUserErp;
	}

	public String getUpdateUserName() {
		return updateUserName;
	}

	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}
}
