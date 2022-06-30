package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.util.Date;

/**
 * 发货封签明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendSealCodeEntity implements Serializable {

	private static final long serialVersionUID = -6116840996994706373L;

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 业务主键
	 */
	private String sendVehicleBizId;
	/**
	 * 操作场地ID
	 */
	private Long operateSiteId;
	/**
	 * 封签号
	 */
	private String sealCode;
	/**
	 * 操作时间
	 */
	private Date operateTime;
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

	public Long getOperateSiteId() {
		return operateSiteId;
	}

	public void setOperateSiteId(Long operateSiteId) {
		this.operateSiteId = operateSiteId;
	}

	public String getSealCode() {
		return sealCode;
	}

	public void setSealCode(String sealCode) {
		this.sealCode = sealCode;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
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
