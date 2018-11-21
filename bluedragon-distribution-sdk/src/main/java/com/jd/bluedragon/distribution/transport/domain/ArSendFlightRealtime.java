package com.jd.bluedragon.distribution.transport.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: ArSendFlightRealtime
 * @Description: -实体类
 * @author wuyoude
 * @date 2018年11月21日 11:11:13
 *
 */
public class ArSendFlightRealtime extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 航班号 */
	private String flightNumber;

	 /** 飞行日期 */
	private Date flightDate;

	 /** 飞行状态 */
	private Integer status;

	 /** 实际起飞时间 */
	private Date realTime;

	 /** 始发中心编码 */
	private String beginNodeCode;

	 /** 始发中心名称 */
	private String beginNodeName;

	 /** 目的中心编码 */
	private String endNodeCode;

	 /** 目的中心名称 */
	private String endNodeName;

	 /** 预计起飞时间 */
	private String takeOffTime;

	 /** 预计降落时间 */
	private String touchDownTime;

	 /** 是否延迟 */
	private Integer delayFlag;

	/**
	 * The set method for flightNumber.
	 * @param flightNumber
	 */
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	/**
	 * The get method for flightNumber.
	 * @return this.flightNumber
	 */
	public String getFlightNumber() {
		return this.flightNumber;
	}

	/**
	 * The set method for flightDate.
	 * @param flightDate
	 */
	public void setFlightDate(Date flightDate) {
		this.flightDate = flightDate;
	}

	/**
	 * The get method for flightDate.
	 * @return this.flightDate
	 */
	public Date getFlightDate() {
		return this.flightDate;
	}

	/**
	 * The set method for status.
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * The get method for status.
	 * @return this.status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 * The set method for realTime.
	 * @param realTime
	 */
	public void setRealTime(Date realTime) {
		this.realTime = realTime;
	}

	/**
	 * The get method for realTime.
	 * @return this.realTime
	 */
	public Date getRealTime() {
		return this.realTime;
	}

	/**
	 * The set method for beginNodeCode.
	 * @param beginNodeCode
	 */
	public void setBeginNodeCode(String beginNodeCode) {
		this.beginNodeCode = beginNodeCode;
	}

	/**
	 * The get method for beginNodeCode.
	 * @return this.beginNodeCode
	 */
	public String getBeginNodeCode() {
		return this.beginNodeCode;
	}

	/**
	 * The set method for beginNodeName.
	 * @param beginNodeName
	 */
	public void setBeginNodeName(String beginNodeName) {
		this.beginNodeName = beginNodeName;
	}

	/**
	 * The get method for beginNodeName.
	 * @return this.beginNodeName
	 */
	public String getBeginNodeName() {
		return this.beginNodeName;
	}

	/**
	 * The set method for endNodeCode.
	 * @param endNodeCode
	 */
	public void setEndNodeCode(String endNodeCode) {
		this.endNodeCode = endNodeCode;
	}

	/**
	 * The get method for endNodeCode.
	 * @return this.endNodeCode
	 */
	public String getEndNodeCode() {
		return this.endNodeCode;
	}

	/**
	 * The set method for endNodeName.
	 * @param endNodeName
	 */
	public void setEndNodeName(String endNodeName) {
		this.endNodeName = endNodeName;
	}

	/**
	 * The get method for endNodeName.
	 * @return this.endNodeName
	 */
	public String getEndNodeName() {
		return this.endNodeName;
	}

	/**
	 * The set method for takeOffTime.
	 * @param takeOffTime
	 */
	public void setTakeOffTime(String takeOffTime) {
		this.takeOffTime = takeOffTime;
	}

	/**
	 * The get method for takeOffTime.
	 * @return this.takeOffTime
	 */
	public String getTakeOffTime() {
		return this.takeOffTime;
	}

	/**
	 * The set method for touchDownTime.
	 * @param touchDownTime
	 */
	public void setTouchDownTime(String touchDownTime) {
		this.touchDownTime = touchDownTime;
	}

	/**
	 * The get method for touchDownTime.
	 * @return this.touchDownTime
	 */
	public String getTouchDownTime() {
		return this.touchDownTime;
	}

	/**
	 * The set method for delayFlag.
	 * @param delayFlag
	 */
	public void setDelayFlag(Integer delayFlag) {
		this.delayFlag = delayFlag;
	}

	/**
	 * The get method for delayFlag.
	 * @return this.delayFlag
	 */
	public Integer getDelayFlag() {
		return this.delayFlag;
	}


}
