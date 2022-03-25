package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;

/**
 * 人员签到-咚咚通知job明细实体类
 * 
 * @author wuyoude
 * @date 2022年3月20日 14:30:43
 *
 */
public class UserSignNoticeJobItemVo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 班次编码
	 */
	private Integer waveCode;
	/**
	 * 工种
	 */
	private Integer jobCode;
	/**
	 * 工种名称
	 */
	private String jobName;	
	/**
	 * 出勤人数总计
	 */
	private Integer attendNumSum;
	
	public Integer getWaveCode() {
		return waveCode;
	}
	public void setWaveCode(Integer waveCode) {
		this.waveCode = waveCode;
	}
	public Integer getJobCode() {
		return jobCode;
	}
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public Integer getAttendNumSum() {
		return attendNumSum;
	}
	public void setAttendNumSum(Integer attendNumSum) {
		this.attendNumSum = attendNumSum;
	}
}
