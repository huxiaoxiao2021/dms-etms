package com.jd.bluedragon.distribution.station.domain;

import java.util.Date;

import com.jd.bluedragon.distribution.station.enums.SignFlowStatusEnum;

import java.io.Serializable;

/**
 * @ClassName: UserSignRecordFlow
 * @Description: 人员签到流程-实体类
 * @author wuyoude
 * @date 2023年03月10日 11:01:53
 *
 */
public class UserSignRecordFlow implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 关联签到表id
	 */
	private Long refRecordId;
	
	/**
	 * 流程-单号
	 */
	private String refFlowBizCode;
	/**
	 * 流程操作类型
	 */
	private Integer flowType;
	/**
	 * 流程状态
	 */
	private Integer flowStatus = SignFlowStatusEnum.DEFALUT.getCode();
	/**
	 * 流程-审批|驳回信息
	 */
	private String flowRemark;
	/**
	 * 流程创建人ERP
	 */
	private String flowCreateUser;
	/**
	 * 流程创建时间
	 */
	private Date flowCreateTime;
	/**
	 * 流程修改人ERP（最后一次审批通过操作人）
	 */
	private String flowUpdateUser;
	/**
	 * 流程更新时间（最后一次审批通过时间）
	 */
	private Date flowUpdateTime;
	/**
	 * 签到日期：根据签到时间，计算归属日期
	 */
	private Date signDateNew;	
	/**
	 * 修改后-签到时间
	 */
	private Date signInTimeNew;

	/**
	 * 修改后-签退时间
	 */
	private Date signOutTimeNew;
	
	/**
	 * 机构编码
	 */
	private Integer orgCode;

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
	 * 员工ERP|拼音|身份证号
	 */
	private String userCode;
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
	 * 签到人员名称
	 */
	private String userName;

	/**
	 * 签到次数
	 */
	private Integer quotaStationTime;

	/**
	 * 签到模式
	 */
	private Integer modeType;
	/**
	 * 签到来源
	 */
	private Integer bizSource;	
	/**
	 * 岗位码
	 */
	private String positionCode;
	
	/**
	 * 操作类型-名称
	 */
	private String flowTypeName;
	
	/**
	 * 审批状态-名称
	 */
	private String flowStatusName;
	/**
	 * 员工ERP|拼音|身份证号--隐藏
	 */
	private String userCodeHidden;	
	/**
	 * 员工名称-隐藏
	 */
	private String userNameHidden;	
	
	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 * @return id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 *
	 * @param orgCode
	 */
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 *
	 * @return orgCode
	 */
	public Integer getOrgCode() {
		return this.orgCode;
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

	/**
	 *
	 * @param siteCode
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 *
	 * @return siteCode
	 */
	public Integer getSiteCode() {
		return this.siteCode;
	}

	/**
	 *
	 * @param userCode
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	/**
	 *
	 * @return userCode
	 */
	public String getUserCode() {
		return this.userCode;
	}

	/**
	 *
	 * @param waveCode
	 */
	public void setWaveCode(Integer waveCode) {
		this.waveCode = waveCode;
	}

	/**
	 *
	 * @return waveCode
	 */
	public Integer getWaveCode() {
		return this.waveCode;
	}

	/**
	 *
	 * @param jobCode
	 */
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}

	/**
	 *
	 * @return jobCode
	 */
	public Integer getJobCode() {
		return this.jobCode;
	}

	/**
	 *
	 * @param signDate
	 */
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	/**
	 *
	 * @return signDate
	 */
	public Date getSignDate() {
		return this.signDate;
	}

	/**
	 *
	 * @param signInTime
	 */
	public void setSignInTime(Date signInTime) {
		this.signInTime = signInTime;
	}

	/**
	 *
	 * @return signInTime
	 */
	public Date getSignInTime() {
		return this.signInTime;
	}

	/**
	 *
	 * @param signOutTime
	 */
	public void setSignOutTime(Date signOutTime) {
		this.signOutTime = signOutTime;
	}

	/**
	 *
	 * @return signOutTime
	 */
	public Date getSignOutTime() {
		return this.signOutTime;
	}

	/**
	 *
	 * @param refPlanKey
	 */
	public void setRefPlanKey(String refPlanKey) {
		this.refPlanKey = refPlanKey;
	}

	/**
	 *
	 * @return refPlanKey
	 */
	public String getRefPlanKey() {
		return this.refPlanKey;
	}

	/**
	 *
	 * @param refGridKey
	 */
	public void setRefGridKey(String refGridKey) {
		this.refGridKey = refGridKey;
	}

	/**
	 *
	 * @return refGridKey
	 */
	public String getRefGridKey() {
		return this.refGridKey;
	}

	/**
	 *
	 * @param refStationKey
	 */
	public void setRefStationKey(String refStationKey) {
		this.refStationKey = refStationKey;
	}

	/**
	 *
	 * @return refStationKey
	 */
	public String getRefStationKey() {
		return this.refStationKey;
	}

	/**
	 *
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 *
	 * @return createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 *
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 *
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 *
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 *
	 * @return updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}

	/**
	 *
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 *
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 *
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
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

	/**
	 *
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 *
	 * @return ts
	 */
	public Date getTs() {
		return this.ts;
	}

	/**
	 *
	 * @param orgName
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 *
	 * @return orgName
	 */
	public String getOrgName() {
		return this.orgName;
	}

	/**
	 *
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 *
	 * @return siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 *
	 * @param floor
	 */
	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	/**
	 *
	 * @return floor
	 */
	public Integer getFloor() {
		return this.floor;
	}

	/**
	 *
	 * @param gridNo
	 */
	public void setGridNo(String gridNo) {
		this.gridNo = gridNo;
	}

	/**
	 *
	 * @return gridNo
	 */
	public String getGridNo() {
		return this.gridNo;
	}

	/**
	 *
	 * @param gridCode
	 */
	public void setGridCode(String gridCode) {
		this.gridCode = gridCode;
	}

	/**
	 *
	 * @return gridCode
	 */
	public String getGridCode() {
		return this.gridCode;
	}

	/**
	 *
	 * @param gridName
	 */
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	/**
	 *
	 * @return gridName
	 */
	public String getGridName() {
		return this.gridName;
	}

	/**
	 *
	 * @param areaCode
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 *
	 * @return areaCode
	 */
	public String getAreaCode() {
		return this.areaCode;
	}

	/**
	 *
	 * @param areaName
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	/**
	 *
	 * @return areaName
	 */
	public String getAreaName() {
		return this.areaName;
	}

	/**
	 *
	 * @param workCode
	 */
	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}

	/**
	 *
	 * @return workCode
	 */
	public String getWorkCode() {
		return this.workCode;
	}

	/**
	 *
	 * @param workName
	 */
	public void setWorkName(String workName) {
		this.workName = workName;
	}

	/**
	 *
	 * @return workName
	 */
	public String getWorkName() {
		return this.workName;
	}

	/**
	 *
	 * @param planName
	 */
	public void setPlanName(String planName) {
		this.planName = planName;
	}

	/**
	 *
	 * @return planName
	 */
	public String getPlanName() {
		return this.planName;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getQuotaStationTime() {
		return quotaStationTime;
	}

	public void setQuotaStationTime(Integer quotaStationTime) {
		this.quotaStationTime = quotaStationTime;
	}

	public Integer getModeType() {
		return modeType;
	}

	public void setModeType(Integer modeType) {
		this.modeType = modeType;
	}

	public Integer getBizSource() {
		return bizSource;
	}

	public void setBizSource(Integer bizSource) {
		this.bizSource = bizSource;
	}

	public Long getRefRecordId() {
		return refRecordId;
	}

	public void setRefRecordId(Long refRecordId) {
		this.refRecordId = refRecordId;
	}

	public Integer getFlowType() {
		return flowType;
	}

	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}

	public Date getSignInTimeNew() {
		return signInTimeNew;
	}

	public void setSignInTimeNew(Date signInTimeNew) {
		this.signInTimeNew = signInTimeNew;
	}

	public Date getSignOutTimeNew() {
		return signOutTimeNew;
	}

	public void setSignOutTimeNew(Date signOutTimeNew) {
		this.signOutTimeNew = signOutTimeNew;
	}

	public String getRefFlowBizCode() {
		return refFlowBizCode;
	}

	public void setRefFlowBizCode(String refFlowBizCode) {
		this.refFlowBizCode = refFlowBizCode;
	}

	public Integer getFlowStatus() {
		return flowStatus;
	}

	public void setFlowStatus(Integer flowStatus) {
		this.flowStatus = flowStatus;
	}

	public String getFlowRemark() {
		return flowRemark;
	}

	public void setFlowRemark(String flowRemark) {
		this.flowRemark = flowRemark;
	}

	public String getFlowCreateUser() {
		return flowCreateUser;
	}

	public void setFlowCreateUser(String flowCreateUser) {
		this.flowCreateUser = flowCreateUser;
	}

	public Date getFlowCreateTime() {
		return flowCreateTime;
	}

	public void setFlowCreateTime(Date flowCreateTime) {
		this.flowCreateTime = flowCreateTime;
	}

	public String getFlowUpdateUser() {
		return flowUpdateUser;
	}

	public void setFlowUpdateUser(String flowUpdateUser) {
		this.flowUpdateUser = flowUpdateUser;
	}

	public Date getFlowUpdateTime() {
		return flowUpdateTime;
	}

	public void setFlowUpdateTime(Date flowUpdateTime) {
		this.flowUpdateTime = flowUpdateTime;
	}

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}

	public String getFlowTypeName() {
		return flowTypeName;
	}

	public void setFlowTypeName(String flowTypeName) {
		this.flowTypeName = flowTypeName;
	}

	public String getFlowStatusName() {
		return flowStatusName;
	}

	public void setFlowStatusName(String flowStatusName) {
		this.flowStatusName = flowStatusName;
	}

	public Date getSignDateNew() {
		return signDateNew;
	}

	public void setSignDateNew(Date signDateNew) {
		this.signDateNew = signDateNew;
	}

	public String getUserCodeHidden() {
		return userCodeHidden;
	}

	public void setUserCodeHidden(String userCodeHidden) {
		this.userCodeHidden = userCodeHidden;
	}

	public String getUserNameHidden() {
		return userNameHidden;
	}

	public void setUserNameHidden(String userNameHidden) {
		this.userNameHidden = userNameHidden;
	}
}
