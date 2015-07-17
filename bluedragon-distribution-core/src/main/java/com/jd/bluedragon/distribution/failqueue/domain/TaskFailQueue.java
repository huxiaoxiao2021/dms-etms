package com.jd.bluedragon.distribution.failqueue.domain;

import java.util.Date;

public class TaskFailQueue {
	private Long failqueueId;
	private Long busiId;
	private Integer busiType;
	private String body;
	private Date createTime;
	private Date updateTime;
	private Integer yn;
	private Integer failStatus;
	private Integer excuteCount;
	private Date excuteTime;
	public Long getFailqueueId() {
		return failqueueId;
	}
	public void setFailqueueId(Long failqueueId) {
		this.failqueueId = failqueueId;
	}
	public Long getBusiId() {
		return busiId;
	}
	public void setBusiId(Long busiId) {
		this.busiId = busiId;
	}
	public Integer getBusiType() {
		return busiType;
	}
	public void setBusiType(Integer busiType) {
		this.busiType = busiType;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}
	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public Integer getFailStatus() {
		return failStatus;
	}
	public void setFailStatus(Integer failStatus) {
		this.failStatus = failStatus;
	}
	public Integer getExcuteCount() {
		return excuteCount;
	}
	public void setExcuteCount(Integer excuteCount) {
		this.excuteCount = excuteCount;
	}
	public Date getExcuteTime() {
		return excuteTime!=null?(Date)excuteTime.clone():null;
	}
	public void setExcuteTime(Date excuteTime) {
		this.excuteTime = excuteTime!=null?(Date)excuteTime.clone():null;
	}

	
}
