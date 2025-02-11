package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;

/**
 * 人员签到-报表-展示实体类
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class UserSignRecordReportSumVo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 编制人数总计
	 */
	private Integer standardNumSum;
	/**
	 * 计划出勤人数总计
	 */
	private Integer planAttendNumSum;
	/**
	 * 实际出勤人数总计
	 */
	private Integer attendNumSum;
	/**
	 * 实时在岗人数总计
	 */
	private Integer nowAttendNumSum;
	/**
	 * 计划偏离度
	 */
	private String deviationPlanRate;
	
	public Integer getStandardNumSum() {
		return standardNumSum;
	}
	public void setStandardNumSum(Integer standardNumSum) {
		this.standardNumSum = standardNumSum;
	}
	public Integer getPlanAttendNumSum() {
		return planAttendNumSum;
	}
	public void setPlanAttendNumSum(Integer planAttendNumSum) {
		this.planAttendNumSum = planAttendNumSum;
	}
	public Integer getAttendNumSum() {
		return attendNumSum;
	}
	public void setAttendNumSum(Integer attendNumSum) {
		this.attendNumSum = attendNumSum;
	}
	public Integer getNowAttendNumSum() {
		return nowAttendNumSum;
	}
	public void setNowAttendNumSum(Integer nowAttendNumSum) {
		this.nowAttendNumSum = nowAttendNumSum;
	}
	public String getDeviationPlanRate() {
		return deviationPlanRate;
	}
	public void setDeviationPlanRate(String deviationPlanRate) {
		this.deviationPlanRate = deviationPlanRate;
	}
}
