package com.jd.bluedragon.distribution.schedule.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: BdWaybillScheduleMq
 * @Description: 运单调度消息体，https://cf.jd.com/pages/viewpage.action?pageId=287910477
 * @author wuyoude
 * @date 2020年04月29日 17:06:37
 *
 */
public class BdWaybillScheduleMq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 运单号
	 */
	private String waybillCode;

	/**
	 * 调度单号
	 */
	private String scheduleBillCode;
	
	/**
	 * 承运商名称
	 */
	private String carriername;
	
	/**
	 * 调度时间，调度信息变更以此时间为准
	 */
	private Date scheduleTime;
	
	/**
	 * 目的分拣中心
	 */
	private Integer oldSiteId;

	/**
	 * 包裹数
	 */
	private Integer goodNumber;

	/**
	 * 调度单号
	 */
	private String sendPay;
	
	/**
	 * 调度单号
	 */
	private String waybillSign;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getScheduleBillCode() {
		return scheduleBillCode;
	}

	public void setScheduleBillCode(String scheduleBillCode) {
		this.scheduleBillCode = scheduleBillCode;
	}

	public String getCarriername() {
		return carriername;
	}

	public void setCarriername(String carriername) {
		this.carriername = carriername;
	}

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Integer getOldSiteId() {
		return oldSiteId;
	}

	public void setOldSiteId(Integer oldSiteId) {
		this.oldSiteId = oldSiteId;
	}

	public Integer getGoodNumber() {
		return goodNumber;
	}

	public void setGoodNumber(Integer goodNumber) {
		this.goodNumber = goodNumber;
	}

	public String getSendPay() {
		return sendPay;
	}

	public void setSendPay(String sendPay) {
		this.sendPay = sendPay;
	}

	public String getWaybillSign() {
		return waybillSign;
	}

	public void setWaybillSign(String waybillSign) {
		this.waybillSign = waybillSign;
	}
}
