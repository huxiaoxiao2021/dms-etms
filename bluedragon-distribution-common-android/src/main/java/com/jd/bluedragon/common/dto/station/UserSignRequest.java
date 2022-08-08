package com.jd.bluedragon.common.dto.station;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: UserSignInRequest
 * @Description: 人员签到、签退请求
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public class UserSignRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 签到记录Id,签退具体某条记录
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
	 * 扫描的三定人员编码
	 */
	private String scanUserCode;
    /**
     * 岗位编码
     */
    private String positionCode;
	/**
	 * 场地编码
	 */
	private Integer siteCode;
	/**
	 * 签到日期
	 */
	private Date signDate;
	
	/**
	 * 操作人code
	 */
	private String operateUserCode;
	/**
	 * 操作人name
	 */
	private String operateUserName;
	/**
	 * 是否拍身份证
	 */
	private Integer modeType;
	
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
	public String getScanUserCode() {
		return scanUserCode;
	}
	public void setScanUserCode(String scanUserCode) {
		this.scanUserCode = scanUserCode;
	}
	public String getPositionCode() {
		return positionCode;
	}
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
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

	public Integer getModeType() {
		return modeType;
	}

	public void setModeType(Integer modeType) {
		this.modeType = modeType;
	}
}
