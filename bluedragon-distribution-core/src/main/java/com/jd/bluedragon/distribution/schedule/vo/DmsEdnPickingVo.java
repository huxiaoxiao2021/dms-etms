package com.jd.bluedragon.distribution.schedule.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.log.BusinessLogDto;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
/**
 * 企配仓拣货
 * @author wuyoude
 *
 */
public class DmsEdnPickingVo implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 调度单号
	 */
	private String scheduleBillCode;
	
	/**
	 * 承运商名称
	 */
	private String carrierName;

	/**
	 * 调度时间，调度信息变更以此时间为准
	 */
	private Date scheduleTime;
	/**
	 * 调度明细列表
	 */
	private List<DmsScheduleInfo> dmsScheduleInfoList;
	/**
	 * 调度明细列表
	 */
	private List<BusinessLogDto> dmsEdnOperateLogList;
	
	public String getScheduleBillCode() {
		return scheduleBillCode;
	}

	public void setScheduleBillCode(String scheduleBillCode) {
		this.scheduleBillCode = scheduleBillCode;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public List<DmsScheduleInfo> getDmsScheduleInfoList() {
		return dmsScheduleInfoList;
	}

	public void setDmsScheduleInfoList(List<DmsScheduleInfo> dmsScheduleInfoList) {
		this.dmsScheduleInfoList = dmsScheduleInfoList;
	}

	public List<BusinessLogDto> getDmsEdnOperateLogList() {
		return dmsEdnOperateLogList;
	}

	public void setDmsEdnOperateLogList(List<BusinessLogDto> dmsEdnOperateLogList) {
		this.dmsEdnOperateLogList = dmsEdnOperateLogList;
	}
}
