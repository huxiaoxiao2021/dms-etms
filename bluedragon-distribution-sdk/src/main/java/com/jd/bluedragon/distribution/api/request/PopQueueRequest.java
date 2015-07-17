package com.jd.bluedragon.distribution.api.request;

import java.util.Date;

public class PopQueueRequest {
	public String queueNo;
	public Integer createSiteCode;
	public String createSiteName;
	public Integer queueType;
	public String expressCode;
	public String expressName;
	public Integer createUserCode;
	public String createUser;
	public Date operateTime;
	public Integer queueStatus;
	public Date startTime;
	public Date endTime;
	public String keyword;

	public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getQueueType() {
		return queueType;
	}

	public void setQueueType(Integer queueType) {
		this.queueType = queueType;
	}

	public String getExpressCode() {
		return expressCode;
	}

	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getOperateTime() {
		return operateTime != null ? (Date) operateTime.clone() : null;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime != null ? (Date) operateTime.clone() : null;
	}

	public Integer getQueueStatus() {
		return queueStatus;
	}

	public void setQueueStatus(Integer queueStatus) {
		this.queueStatus = queueStatus;
	}

	public Date getStartTime() {
		return startTime != null ? (Date) startTime.clone() : null;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime != null ? (Date) startTime.clone() : null;
	}

	public Date getEndTime() {
		return this.endTime == null ? null : (Date) this.endTime.clone();
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime == null ? null : (Date) endTime.clone();
	}

	public String getCreateSiteName() {
		return createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("{createSiteCode:").append(createSiteCode).append(",");
		str.append("queueType:").append(queueType).append(",");
		str.append("expressCode:").append(expressCode).append(",");
		str.append("expressName:").append(expressName).append(",");
		str.append("keyword:").append(keyword).append(",");
		str.append("createUserCode:").append(createUserCode).append("}");
		return str.toString();
	}

}
