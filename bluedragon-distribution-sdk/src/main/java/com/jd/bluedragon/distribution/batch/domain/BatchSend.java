package com.jd.bluedragon.distribution.batch.domain;

import java.util.Date;

public class BatchSend {
	 /**
     * 全局唯一ID
     */
    private Long id;

    /**
     * 波次号
     */
    private String batchCode;

    /**
     * 创建站点编号
     */
    private Integer createSiteCode;
    
    /**
     * 目的站点编号
     */
    private Integer receiveSiteCode;
    
    /**
     * 批次号
     */
    private String sendCode;
    

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人编号
     */
    private Integer createUserCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后操作人编号
     */
    private Integer updateUserCode;

    /**
     * 最后操作人
     */
    private String updateUser;
    
    /**
     * 最后操作人
     */
    private Integer sendStatus;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 是否删除 '0' 删除 '1' 使用
     */
    private Integer yn;

    /**
     * 发车状态【null:初始状态；1：已发车；2:已取消发车】
     */
    private Integer sendCarState;

    /**
     * 发车状态操作时间
     */
    private Date    sendCarOperateTime;

    public Integer getSendCarState() {
        return sendCarState;
    }

    public void setSendCarState(Integer sendCarState) {
        this.sendCarState = sendCarState;
    }

    public Date getSendCarOperateTime() {
        return sendCarOperateTime;
    }

    public void setSendCarOperateTime(Date sendCarOperateTime) {
        this.sendCarOperateTime = sendCarOperateTime;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((batchCode == null) ? 0 : batchCode.hashCode());
		result = prime * result
				+ ((sendCode == null) ? 0 : sendCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BatchSend other = (BatchSend) obj;
		if (batchCode == null) {
			if (other.batchCode != null)
				return false;
		} else if (!batchCode.equals(other.batchCode))
			return false;
		if (sendCode == null) {
			if (other.sendCode != null)
				return false;
		} else if (!sendCode.equals(other.sendCode))
			return false;
		return true;
	}
    
    
}
