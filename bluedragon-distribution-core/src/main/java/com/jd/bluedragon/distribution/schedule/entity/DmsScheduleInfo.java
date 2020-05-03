package com.jd.bluedragon.distribution.schedule.entity;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * @ClassName: DmsScheduleInfo
 * @Description: 分拣调度信息表，数据来源于运单和企配仓-实体类
 * @author wuyoude
 * @date 2020年04月30日 14:35:28
 *
 */
public class DmsScheduleInfo extends DbEntity {

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
	 * 包裹数
	 */
	private Integer packageNum;

	/**
	 * 承运商名称
	 */
	private String carrierName;

	/**
	 * 调度时间，调度信息变更以此时间为准
	 */
	private Date scheduleTime;

	/**
	 * 业务数据更新时间，企配仓信息变更以此时间为准
	 */
	private Date businessUpdateTime;

	/**
	 * 1-有效，0-无效
	 */
	private Integer yn;

	/**
	 *
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 *
	 * @return waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 *
	 * @param scheduleBillCode
	 */
	public void setScheduleBillCode(String scheduleBillCode) {
		this.scheduleBillCode = scheduleBillCode;
	}

	/**
	 *
	 * @return scheduleBillCode
	 */
	public String getScheduleBillCode() {
		return this.scheduleBillCode;
	}

	/**
	 *
	 * @param businessType
	 */
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	/**
	 *
	 * @return businessType
	 */
	public Integer getBusinessType() {
		return this.businessType;
	}

	/**
	 *
	 * @param businessBatchCode
	 */
	public void setBusinessBatchCode(String businessBatchCode) {
		this.businessBatchCode = businessBatchCode;
	}

	/**
	 *
	 * @return businessBatchCode
	 */
	public String getBusinessBatchCode() {
		return this.businessBatchCode;
	}

	/**
	 *
	 * @param parentOrderId
	 */
	public void setParentOrderId(String parentOrderId) {
		this.parentOrderId = parentOrderId;
	}

	/**
	 *
	 * @return parentOrderId
	 */
	public String getParentOrderId() {
		return this.parentOrderId;
	}

	/**
	 *
	 * @param destDmsSiteCode
	 */
	public void setDestDmsSiteCode(Integer destDmsSiteCode) {
		this.destDmsSiteCode = destDmsSiteCode;
	}

	/**
	 *
	 * @return destDmsSiteCode
	 */
	public Integer getDestDmsSiteCode() {
		return this.destDmsSiteCode;
	}

	/**
	 *
	 * @param packageNum
	 */
	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}

	/**
	 *
	 * @return packageNum
	 */
	public Integer getPackageNum() {
		return this.packageNum;
	}

	/**
	 *
	 * @param carrierName
	 */
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	/**
	 *
	 * @return carrierName
	 */
	public String getCarrierName() {
		return this.carrierName;
	}

	/**
	 *
	 * @param scheduleTime
	 */
	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	/**
	 *
	 * @return scheduleTime
	 */
	public Date getScheduleTime() {
		return this.scheduleTime;
	}

	/**
	 *
	 * @param businessUpdateTime
	 */
	public void setBusinessUpdateTime(Date businessUpdateTime) {
		this.businessUpdateTime = businessUpdateTime;
	}

	/**
	 *
	 * @return businessUpdateTime
	 */
	public Date getBusinessUpdateTime() {
		return this.businessUpdateTime;
	}

	/**
	 *
	 * @param yn
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Integer getYn() {
		return this.yn;
	}


}
