package com.jd.bluedragon.distribution.interceptconfig.domain;

import java.util.Date;

public class InterceptConfigInfo {

	private Long id;

	private Integer interceptType;

	private String interceptCode;

	private String interceptMessage;

	private String guidanceNotes;

	private String createUserErp;

	private String createUserName;

	private String updateUserErp;

	private String updateUserName;

	private Date createTime;

	private Date updateTime;

	private Integer isDelete;

	private Date ts;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getInterceptType() {
		return interceptType;
	}

	public void setInterceptType(Integer interceptType) {
		this.interceptType = interceptType;
	}

	public String getInterceptCode() {
		return interceptCode;
	}

	public void setInterceptCode(String interceptCode) {
		this.interceptCode = interceptCode;
	}

	public String getInterceptMessage() {
		return interceptMessage;
	}

	public void setInterceptMessage(String interceptMessage) {
		this.interceptMessage = interceptMessage;
	}

	public String getGuidanceNotes() {
		return guidanceNotes;
	}

	public void setGuidanceNotes(String guidanceNotes) {
		this.guidanceNotes = guidanceNotes;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}
}
