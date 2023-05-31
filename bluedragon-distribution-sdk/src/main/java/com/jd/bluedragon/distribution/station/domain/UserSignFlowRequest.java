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
	 * 流程操作类型
	 */
	private Integer flowType;
	/**
	 * 关联签到表id
	 */
	private Long recordId;
	/**
	 * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工
	 */
	private Integer jobCode;
	/**
	 * 员工ERP|拼音|身份证号
	 */
	private String userCode;	
	/**
	 * 岗位码
	 */
	private String positionCode;
	/**
	 * 修改后-签到时间
	 */
	private String signInTimeNewStr;

	/**
	 * 修改后-签退时间
	 */
	private String signOutTimeNewStr;
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
	
	public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public Integer getJobCode() {
		return jobCode;
	}
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getPositionCode() {
		return positionCode;
	}
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
	public String getSignInTimeNewStr() {
		return signInTimeNewStr;
	}
	public void setSignInTimeNewStr(String signInTimeNewStr) {
		this.signInTimeNewStr = signInTimeNewStr;
	}
	public String getSignOutTimeNewStr() {
		return signOutTimeNewStr;
	}
	public void setSignOutTimeNewStr(String signOutTimeNewStr) {
		this.signOutTimeNewStr = signOutTimeNewStr;
	}
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
	public Integer getFlowType() {
		return flowType;
	}
	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}
}
