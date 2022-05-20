package com.jd.bluedragon.common.dto.station;

import java.io.Serializable;
import java.util.Date;

import com.jd.bluedragon.common.dto.group.GroupMemberData;

/**
 * @ClassName: UserSignRecordData
 * @Description: 人员签到记录数据
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public class UserSignRecordData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 机构编码
	 */
	private Integer orgCode;

	/**
	 * 战区
	 */
	private String warZoneCode;
	private String warZoneName;

	/**
	 * 场地编码
	 */
	private Integer siteCode;

	/**
	 * 员工ERP|拼音|身份证号
	 */
	private String userCode;
	/**
	 * 签到人员名称
	 */
	private String userName;

	/**
	 * 班次:1-白班 2-中班 3-晚班
	 */
	private Integer waveCode;

	/**
	 * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工
	 */
	private Integer jobCode;

	/**
	 * 签到日期：根据签到时间，计算归属日期
	 */
	private Date signDate;

	/**
	 * 签到时间
	 */
	private Date signInTime;

	/**
	 * 签退时间
	 */
	private Date signOutTime;

	/**
	 * 关联：work_station_attend_plan业务主键
	 */
	private String refPlanKey;

	/**
	 * ref：work_station_grid业务主键
	 */
	private String refGridKey;

	/**
	 * 关联：work_station业务主键
	 */
	private String refStationKey;

	/**
	 * 创建人ERP
	 */
	private String createUser;

	/**
	 * 创建人姓名
	 */
	private String createUserName;

	/**
	 * 修改人ERP
	 */
	private String updateUser;

	/**
	 * 更新人姓名
	 */
	private String updateUserName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 逻辑删除标志,0-删除,1-正常
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 * 机构名称
	 */
	private String orgName;

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
	 * 方案名称
	 */
	private String planName;
	/**
	 * 班次名称
	 */
	private String waveName;
	/**
	 * 工种名称
	 */
	private String jobName;
	/**
	 * 工作时长（单位：小时）
	 */
	private String workHours;
	/**
	 * 工作时长（单位：hh时mm分）
	 */
	private String workTimes;
	
	private GroupMemberData groupData;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
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
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public Date getSignInTime() {
		return signInTime;
	}
	public void setSignInTime(Date signInTime) {
		this.signInTime = signInTime;
	}
	public Date getSignOutTime() {
		return signOutTime;
	}
	public void setSignOutTime(Date signOutTime) {
		this.signOutTime = signOutTime;
	}
	public String getRefPlanKey() {
		return refPlanKey;
	}
	public void setRefPlanKey(String refPlanKey) {
		this.refPlanKey = refPlanKey;
	}
	public String getRefGridKey() {
		return refGridKey;
	}
	public void setRefGridKey(String refGridKey) {
		this.refGridKey = refGridKey;
	}
	public String getRefStationKey() {
		return refStationKey;
	}
	public void setRefStationKey(String refStationKey) {
		this.refStationKey = refStationKey;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getUpdateUserName() {
		return updateUserName;
	}
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public Date getTs() {
		return ts;
	}
	public void setTs(Date ts) {
		this.ts = ts;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
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
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getWaveName() {
		return waveName;
	}
	public void setWaveName(String waveName) {
		this.waveName = waveName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getWorkHours() {
		return workHours;
	}
	public void setWorkHours(String workHours) {
		this.workHours = workHours;
	}
	public String getWorkTimes() {
		return workTimes;
	}
	public void setWorkTimes(String workTimes) {
		this.workTimes = workTimes;
	}
	public GroupMemberData getGroupData() {
		return groupData;
	}
	public void setGroupData(GroupMemberData groupData) {
		this.groupData = groupData;
	}
}
