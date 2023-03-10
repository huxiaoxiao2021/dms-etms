package com.jd.bluedragon.common.dto.station;

import java.io.Serializable;

/**
 * 登录扫描请求
 * @author wuyoude
 *
 */
public class ScanForLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * 扫描的三定人员编码
	 */
	private String scanUserCode;
    /**
     * 岗位编码
     */
    private String positionCode;
	/**
	 * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工
	 */
	private Integer jobCode;
	/**
	 * 签到人姓名
	 */
	private String userName;
	/**
	 * 员工ERP|拼音|身份证号
	 */
	private String userCode;    
    
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
	public Integer getJobCode() {
		return jobCode;
	}
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}


}
