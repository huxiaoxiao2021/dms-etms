package com.jd.bluedragon.distribution.schedule.entity;

import java.util.Date;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: DmsScheduleInfoCondition
 * @Description: 查询条件
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public class DmsScheduleInfoCondition extends BasePagerCondition {

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
	 * 业务类型（1-企配仓调度）
	 */
	private Integer businessType;

	/**
	 * 业务类型为1时为企配仓批次号
	 */
	private String businessBatchCode;

	/**
	 * 父单号
	 */
	private String parentOrderId;

	/**
	 * 目的分拣中心
	 */
	private Integer destDmsSiteCode;

	/**
	 * 调度时间开始时间
	 */
	private Date scheduleTimeLt;
	/**
	 * 调度时间开始时间
	 */
	private String scheduleTimeLtStr;	
	/**
	 * 调度时间结束时间
	 */
	private Date scheduleTimeGte;	
	/**
	 * 调度时间结束时间
	 */
	private String scheduleTimeGteStr;
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
	public Integer getBusinessType() {
		return businessType;
	}
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}
	public String getBusinessBatchCode() {
		return businessBatchCode;
	}
	public void setBusinessBatchCode(String businessBatchCode) {
		this.businessBatchCode = businessBatchCode;
	}
	public String getParentOrderId() {
		return parentOrderId;
	}
	public void setParentOrderId(String parentOrderId) {
		this.parentOrderId = parentOrderId;
	}
	public Integer getDestDmsSiteCode() {
		return destDmsSiteCode;
	}
	public void setDestDmsSiteCode(Integer destDmsSiteCode) {
		this.destDmsSiteCode = destDmsSiteCode;
	}
	public Date getScheduleTimeLt() {
		return scheduleTimeLt;
	}
	public void setScheduleTimeLt(Date scheduleTimeLt) {
		this.scheduleTimeLt = scheduleTimeLt;
	}
	public String getScheduleTimeLtStr() {
		return scheduleTimeLtStr;
	}
	public void setScheduleTimeLtStr(String scheduleTimeLtStr) {
		this.scheduleTimeLtStr = scheduleTimeLtStr;
	}
	public Date getScheduleTimeGte() {
		return scheduleTimeGte;
	}
	public void setScheduleTimeGte(Date scheduleTimeGte) {
		this.scheduleTimeGte = scheduleTimeGte;
	}
	public String getScheduleTimeGteStr() {
		return scheduleTimeGteStr;
	}
	public void setScheduleTimeGteStr(String scheduleTimeGteStr) {
		this.scheduleTimeGteStr = scheduleTimeGteStr;
	}
}
