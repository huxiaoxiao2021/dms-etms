package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 人员签到-咚咚通知明细实体类
 * 
 * @author wuyoude
 * @date 2022年3月20日 14:30:43
 *
 */
public class UserSignNoticeWaveItemVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 班次编码
	 */
	private Integer waveCode;
	/**
	 * 班次名称
	 */
	private Integer waveName;	
	/**
	 * 计划人数总计
	 */
	private Integer planAttendNumSum;
	
	/**
	 * 出勤人数总计
	 */
	private Integer attendNumSum;
	/**
	 * 计划偏离度= |attendNum - planAttendNumSum|/planAttendNumSum 
	 */
	private String deviationPlanRate;
	
	/**
	 * 波次明细
	 */
	private List<UserSignNoticeJobItemVo> jobItems;

	public Integer getWaveCode() {
		return waveCode;
	}

	public void setWaveCode(Integer waveCode) {
		this.waveCode = waveCode;
	}

	public Integer getWaveName() {
		return waveName;
	}

	public void setWaveName(Integer waveName) {
		this.waveName = waveName;
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

	public String getDeviationPlanRate() {
		return deviationPlanRate;
	}

	public void setDeviationPlanRate(String deviationPlanRate) {
		this.deviationPlanRate = deviationPlanRate;
	}

	public List<UserSignNoticeJobItemVo> getJobItems() {
		return jobItems;
	}

	public void setJobItems(List<UserSignNoticeJobItemVo> jobItems) {
		this.jobItems = jobItems;
	}
}
