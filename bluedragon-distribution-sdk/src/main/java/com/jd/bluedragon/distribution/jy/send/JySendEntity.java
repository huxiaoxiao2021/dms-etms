package com.jd.bluedragon.distribution.jy.send;


import java.io.Serializable;
import java.util.Date;

/**
 * 发货明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendEntity implements Serializable {

	private static final long serialVersionUID = -3759259851365257617L;

    public JySendEntity() {}

    public JySendEntity(Long createSiteId, String sendVehicleBizId) {
        this.createSiteId = createSiteId;
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public JySendEntity(String barCode, Long createSiteId) {
        this.barCode = barCode;
        this.createSiteId = createSiteId;
    }

    public JySendEntity(String sendVehicleBizId, Long createSiteId, Long receiveSiteId) {
        this.sendVehicleBizId = sendVehicleBizId;
        this.createSiteId = createSiteId;
        this.receiveSiteId = receiveSiteId;
    }

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * send_vehicle业务主键
	 */
	private String sendVehicleBizId;
	/**
	 * 始发场地ID
	 */
	private Long createSiteId;
	/**
	 * 目的场地ID
	 */
	private Long receiveSiteId;
	/**
	 * 单号
	 */
	private String barCode;
	/**
	 * 发货批次号
	 */
	private String sendCode;
	/**
	 * 操作时间
	 */
	private Date operateTime;
	/**
	 * 发货拦截标识；0-否 1-是
	 */
	private Integer interceptFlag;
	/**
	 * 是否强制发货；0-否 1-是
	 */
	private Integer forceSendFlag;
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

	private transient String newSendCode;

	public String getNewSendCode() {
		return newSendCode;
	}

	public void setNewSendCode(String newSendCode) {
		this.newSendCode = newSendCode;
	}

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

	public Long getCreateSiteId() {
		return createSiteId;
	}

	public void setCreateSiteId(Long createSiteId) {
		this.createSiteId = createSiteId;
	}

	public Long getReceiveSiteId() {
		return receiveSiteId;
	}

	public void setReceiveSiteId(Long receiveSiteId) {
		this.receiveSiteId = receiveSiteId;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getInterceptFlag() {
		return interceptFlag;
	}

	public void setInterceptFlag(Integer interceptFlag) {
		this.interceptFlag = interceptFlag;
	}

	public Integer getForceSendFlag() {
		return forceSendFlag;
	}

	public void setForceSendFlag(Integer forceSendFlag) {
		this.forceSendFlag = forceSendFlag;
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
