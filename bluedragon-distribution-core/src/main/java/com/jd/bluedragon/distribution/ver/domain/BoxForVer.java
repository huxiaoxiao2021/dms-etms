package com.jd.bluedragon.distribution.ver.domain;

import com.jd.bluedragon.distribution.box.domain.Box;

import java.util.Date;

public class BoxForVer extends Box {


	public static final String TYPE_BC = "BC"; // 正向普通箱号
	public static final String TYPE_BS = "BS"; // 正向奢侈品箱号
	public static final String TYPE_TC = "TC"; // 退货普通箱号
	public static final String TYPE_TS = "TS"; // 退货奢侈品箱号
	public static final String TYPE_GC = "GC"; // 取件普通箱号
	public static final String TYPE_GS = "GS"; // 取件奢侈品箱号


	public static final Integer TRANSPORT_TYPE_AIR = 1;//运输方式  航空
	public static final Integer TRANSPORT_TYPE_ROAD = 2;//运输方式  公路
	public static final Integer TRANSPORT_TYPE_RAILWAY = 3;//运输方式  铁路


	private String boxCode;

	private Integer opCode;
	private String machineCode;
	private Integer mailCount;
	private String chuteCode;

	/**
	 * 接收标识 0：未接收 1：已接收
	 */
	private Integer receFlag;

	/**
	 * 数据接收时间
	 */
	private String receTime;

	/**
	 * 数据库时间
	 */
	private Date timesTamp;


	//增加关于箱子的发货状态
	private Integer boxStatus;

	private String receiveSiteType;


	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getOpCode() {
		return opCode;
	}

	public void setOpCode(Integer opCode) {
		this.opCode = opCode;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public Integer getMailCount() {
		return mailCount;
	}

	public void setMailCount(Integer mailCount) {
		this.mailCount = mailCount;
	}

	public String getChuteCode() {
		return chuteCode;
	}

	public void setChuteCode(String chuteCode) {
		this.chuteCode = chuteCode;
	}

	public Integer getReceFlag() {
		return receFlag;
	}

	public void setReceFlag(Integer receFlag) {
		this.receFlag = receFlag;
	}

	public String getReceTime() {
		return receTime;
	}

	public void setReceTime(String receTime) {
		this.receTime = receTime;
	}

	public Date getTimesTamp() {
		return timesTamp;
	}

	public void setTimesTamp(Date timesTamp) {
		this.timesTamp = timesTamp;
	}

	public Integer getBoxStatus() {
		return boxStatus;
	}

	public void setBoxStatus(Integer boxStatus) {
		this.boxStatus = boxStatus;
	}

	public String getReceiveSiteType() {
		return receiveSiteType;
	}

	public void setReceiveSiteType(String receiveSiteType) {
		this.receiveSiteType = receiveSiteType;
	}
}