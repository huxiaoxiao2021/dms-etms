package com.jd.bluedragon.distribution.popPrint.domain;

import java.util.Date;
/**
 * 
* 类描述： POP收货排队号
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-14 下午2:25:32
* 版本号： v1.0
 */
public class PopQueue {
	public Long id;
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
	public Integer waitNo;
	public Date createTime;
	public Date updateTime;
	public Integer yn;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
		return this.operateTime == null ? null : (Date) this.operateTime.clone();
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}
	public Integer getQueueStatus() {
		return queueStatus;
	}
	public void setQueueStatus(Integer queueStatus) {
		this.queueStatus = queueStatus;
	}
	public Date getStartTime() {
		return this.startTime == null ? null : (Date) this.startTime.clone();
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime!=null?(Date)startTime.clone():null;
	}
	public Date getEndTime() {
		return this.endTime == null ? null : (Date) this.endTime.clone();
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime!=null?(Date)endTime.clone():null;
	}
	public Integer getWaitNo() {
		return waitNo;
	}
	public void setWaitNo(Integer waitNo) {
		this.waitNo = waitNo;
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
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public String getCreateSiteName() {
		return createSiteName;
	}
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}
	

}
