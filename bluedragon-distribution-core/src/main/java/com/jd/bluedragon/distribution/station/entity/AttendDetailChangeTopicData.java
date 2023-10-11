package com.jd.bluedragon.distribution.station.entity;

import java.io.Serializable;

/**
 * 考勤数据-jmq对象 -实体
 * https://joyspace.jd.com/page/OOmUabySNPO9wTDnSzLQ
 * @author wuyoude
 *
 */
public class AttendDetailChangeTopicData implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 操作标识：1 新增
	 */
	public static final Integer OP_TYPE_ADD = 1;
	/**
	 * 操作标识：2 更新
	 */
	public static final Integer OP_TYPE_UPDATE = 2;
	
	
	private String userCode;
	
	private String userErp;
	/**
	 * 考勤日期
	 */
	private String eday;
	/**
	 * 配送中心编号
	 */
	private String dcNo;
	/**
	 * 配送中心名称
	 */
	private String dcName;
	/**
	 * 实际上班卡
	 */
	private String actualOnTime;
	/**
	 * 实际下班卡
	 */
	private String actualOffTime;
	/**
	 * 操作标识：1 新增、2 更新、0 删除
	 */
	private Integer opType;
	
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserErp() {
		return userErp;
	}
	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}
	public String getEday() {
		return eday;
	}
	public void setEday(String eday) {
		this.eday = eday;
	}
	public String getDcNo() {
		return dcNo;
	}
	public void setDcNo(String dcNo) {
		this.dcNo = dcNo;
	}
	public String getDcName() {
		return dcName;
	}
	public void setDcName(String dcName) {
		this.dcName = dcName;
	}
	public String getActualOnTime() {
		return actualOnTime;
	}
	public void setActualOnTime(String actualOnTime) {
		this.actualOnTime = actualOnTime;
	}
	public String getActualOffTime() {
		return actualOffTime;
	}
	public void setActualOffTime(String actualOffTime) {
		this.actualOffTime = actualOffTime;
	}
	public Integer getOpType() {
		return opType;
	}
	public void setOpType(Integer opType) {
		this.opType = opType;
	}
	
}
