package com.jd.bluedragon.distribution.popPrint.domain;

import java.util.Date;
/**
 * 
* 类描述： POP商家托寄收货
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-31 上午9:54:43
* 版本号： v1.0
 */
public class PopSignin {
	public Integer id;
	public String queueNo;
	public String thirdWaybillCode;
	public Integer createUserCode;
	public String createUser;
	public Date operateTime;
	public String expressCode;
	public String expressName;
	public Integer createSiteCode;
	public String createSiteName;
	public Date createTime;
	public Date updateTime;
	public Integer yn;
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getQueueNo() {
		return queueNo;
	}
	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}
	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}
	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
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
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getCreateSiteName() {
		return createSiteName;
	}
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}
	
	
	

}
