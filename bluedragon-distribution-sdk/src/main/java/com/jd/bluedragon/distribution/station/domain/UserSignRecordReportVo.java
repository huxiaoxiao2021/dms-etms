package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 人员签到-报表-展示实体类
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class UserSignRecordReportVo implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 签到日期：根据签到时间，计算归属日期
	 */
	private Date signDate;	
	/**
	 * 机构编码
	 */
	private Integer orgCode;
	/**
	 * 机构名称
	 */
	private String orgName;
	/**
	 * 战区编码
	 */
	private String warZoneCode;
	/**
	 * 战区名称
	 */
	private String warZoneName;
	/**
	 * 场地编码
	 */
	private Integer siteCode;
	/**
	 * 场地名称
	 */
	private String siteName;	
	/**
	 * 楼层
	 */
	private Integer floor;

	/**
	 * 网格号:01~99
	 */
	private String gridNo;

	/**
	 * 网格ID
	 */
	private String gridCode;

	/**
	 * 网格名称
	 */
	private String gridName;

	/**
	 * 作业区编码
	 */
	private String areaCode;

	/**
	 * 作业区名称
	 */
	private String areaName;

	/**
	 * 工序编码
	 */
	private String workCode;

	/**
	 * 工序名称
	 */
	private String workName;	
	/**
	 * 班次:1-白班 2-中班 3-晚班
	 */
	private Integer waveCode;
	/**
	 * 班次名称
	 */
	private String waveName;
	/**
	 * 方案名称
	 */
	private String planName;
	/**
	 * 编制人数
	 */
	private Integer standardNum;
	/**
	 * 计划出勤人数
	 */
	private Integer planAttendNum;
	/**
	 * 实际出勤人数
	 */
	private Integer attendNum;
	/**
	 * 实时在岗人数
	 */
	private Integer nowAttendNum;
	
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public Integer getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getWarZoneCode() {
		return warZoneCode;
	}

	public void setWarZoneCode(String warZoneCode) {
		this.warZoneCode = warZoneCode;
	}

	public String getWarZoneName() {
		return warZoneName;
	}

	public void setWarZoneName(String warZoneName) {
		this.warZoneName = warZoneName;
	}

	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public String getGridNo() {
		return gridNo;
	}
	public void setGridNo(String gridNo) {
		this.gridNo = gridNo;
	}
	public String getGridCode() {
		return gridCode;
	}
	public void setGridCode(String gridCode) {
		this.gridCode = gridCode;
	}
	public String getGridName() {
		return gridName;
	}
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getWorkCode() {
		return workCode;
	}
	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
	}
	public Integer getWaveCode() {
		return waveCode;
	}
	public void setWaveCode(Integer waveCode) {
		this.waveCode = waveCode;
	}
	public String getWaveName() {
		return waveName;
	}
	public void setWaveName(String waveName) {
		this.waveName = waveName;
	}
	public Integer getStandardNum() {
		return standardNum;
	}
	public void setStandardNum(Integer standardNum) {
		this.standardNum = standardNum;
	}
	public Integer getPlanAttendNum() {
		return planAttendNum;
	}
	public void setPlanAttendNum(Integer planAttendNum) {
		this.planAttendNum = planAttendNum;
	}
	public Integer getAttendNum() {
		return attendNum;
	}
	public void setAttendNum(Integer attendNum) {
		this.attendNum = attendNum;
	}
	public Integer getNowAttendNum() {
		return nowAttendNum;
	}
	public void setNowAttendNum(Integer nowAttendNum) {
		this.nowAttendNum = nowAttendNum;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
}
