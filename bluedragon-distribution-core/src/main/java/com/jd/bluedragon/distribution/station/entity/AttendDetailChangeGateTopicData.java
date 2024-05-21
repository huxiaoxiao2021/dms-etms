package com.jd.bluedragon.distribution.station.entity;

import java.io.Serializable;

/**
 * 考勤数据-jmq对象 -实体
 * https://joyspace.jd.com/page/OOmUabySNPO9wTDnSzLQ
 * @author wuyoude
 *
 */
public class AttendDetailChangeGateTopicData implements Serializable{

	// {"createTime":"2024-05-11T11:31:27","deviceName":"杭州枢纽6号库人脸机内进1","erp":"jiangtingju1","floor":"杭州富阳散货分拣中心",
	// "gateName":"正门","name":"姜庭菊","organization":"杭州富阳散货分拣中心","passResult":"成功","passStatus":"进门","passTime":"2024-05-11T11:31:35",
	// "province":"杭州枢纽","staffType":"正式工"}

	private static final long serialVersionUID = 1L;

	/**
	 * 员工erp
	 */
	private String erpOrIdCard;

	/**
	 * 员工erp
	 */
	private String erp;
	
	/**
	 * 身份证号：三方人员一般无erp，但是有身份证号
	 */
	private String idCard;

	/**
	 * 场地名称
	 */
	private String floor;

	/**
	 * 员工类型
	 */
	private String staffType;

	/**
	 * 实际下班卡
	 */
	private String passTime;

	/**
	 * 实际下班卡
	 */
	private String passStatus;

	public String getPassStatus() {
		return passStatus;
	}

	public void setPassStatus(String passStatus) {
		this.passStatus = passStatus;
	}

	public String getErpOrIdCard() {
		return erpOrIdCard;
	}

	public void setErpOrIdCard(String erpOrIdCard) {
		this.erpOrIdCard = erpOrIdCard;
	}

	public String getPassTime() {
		return passTime;
	}

	public void setPassTime(String passTime) {
		this.passTime = passTime;
	}

	public String getStaffType() {
		return staffType;
	}

	public void setStaffType(String staffType) {
		this.staffType = staffType;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getErp() {
		return erp;
	}

	public void setErp(String erp) {
		this.erp = erp;
	}

}
