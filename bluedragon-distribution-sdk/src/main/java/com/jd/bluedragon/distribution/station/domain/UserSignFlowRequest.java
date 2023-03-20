package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 签到流程-请求
 * @author wuyoude
 *
 */
public class UserSignFlowRequest implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 操作人站点
	 */
	private Integer operateSiteCode;
	/**
	 * 操作人编码
	 */
	private String operateUserCode;
	/**
	 * 操作人名称
	 */
	private String operateUserName;
	/**
	 * 操作时间
	 */
	private Date operateTime;
	/**
	 * 签到数据
	 */
	private UserSignRecordFlow userSignRecordFlow;
	
	public Integer getOperateSiteCode() {
		return operateSiteCode;
	}
	public void setOperateSiteCode(Integer operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}
	public String getOperateUserCode() {
		return operateUserCode;
	}
	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}
	public String getOperateUserName() {
		return operateUserName;
	}
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public UserSignRecordFlow getUserSignRecordFlow() {
		return userSignRecordFlow;
	}
	public void setUserSignRecordFlow(UserSignRecordFlow userSignRecordFlow) {
		this.userSignRecordFlow = userSignRecordFlow;
	}
}
