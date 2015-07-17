package com.jd.bluedragon.distribution.popReveice.domain;

import java.util.Date;

public class TaskPopRecieveCount {
	public Long taskId;
	public String waybillCode;
	public String thirdWaybillCode;
	public String expressCode;
	public String expressName;
	public Integer actualNum;
	public Date operateTime;
	public Date executeTime;
	public Integer yn;
	public Integer taskStatus;
	public Integer taskType;
	public Integer executeCount;
	public String ownSign;
	public Date createTime;
	public Date updateTime;
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}
	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
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
	public Integer getActualNum() {
		return actualNum;
	}
	public void setActualNum(Integer actualNum) {
		this.actualNum = actualNum;
	}
	public Date getOperateTime() {
		return this.operateTime == null ? null : (Date) this.operateTime.clone();
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}
	
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public Integer getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(Integer taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Integer getTaskType() {
		return taskType;
	}
	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public String getOwnSign() {
		return ownSign;
	}
	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}
	public Date getCreateTime() {
		return this.createTime == null ? null : (Date) this.createTime.clone();
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}
	public Date getUpdateTime() {
		return this.updateTime == null ? null : (Date) this.updateTime.clone();
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}
	public Date getExecuteTime() {
		return this.executeTime == null ? null : (Date) this.executeTime.clone();
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime!=null?(Date)executeTime.clone():null;
	}
	public Integer getExecuteCount() {
		return executeCount;
	}
	public void setExecuteCount(Integer executeCount) {
		this.executeCount = executeCount;
	}
	
	

}
