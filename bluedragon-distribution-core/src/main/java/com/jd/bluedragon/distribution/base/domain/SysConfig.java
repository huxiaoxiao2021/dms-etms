package com.jd.bluedragon.distribution.base.domain;

import java.util.Date;

public class SysConfig {

	private Long configId;
	private Integer configType;
	private String configName;
	private String configContent;
	private Integer configOrder;
	private String memo;
	private Integer yn;
	private Date createTime;
	private Date updateTime;
	private String oldPassword;
	private String newPassword;
	private String redisContent;

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public Integer getConfigType() {
		return configType;
	}

	public void setConfigType(Integer configType) {
		this.configType = configType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigContent() {
		return configContent;
	}

	public void setConfigContent(String configContent) {
		this.configContent = configContent;
	}

	public Integer getConfigOrder() {
		return configOrder;
	}

	public void setConfigOrder(Integer configOrder) {
		this.configOrder = configOrder;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Date getCreateTime() {
		return createTime != null ? (Date) createTime.clone() : null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime != null ? (Date) createTime.clone() : null;
	}

	public Date getUpdateTime() {
		return updateTime != null ? (Date) updateTime.clone() : null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime != null ? (Date) updateTime.clone() : null;
	}

	public String getOldPassword() {
		return oldPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRedisContent() {
		return redisContent;
	}

	public void setRedisContent(String redisContent) {
		this.redisContent = redisContent;
	}
	
	

}
