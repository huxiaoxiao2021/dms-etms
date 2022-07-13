package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;
import java.util.Date;
/**
 * @ClassName: JyGroupMemberData
 * @Description: 小组成员数据
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public class JyGroupMemberData implements Serializable {

	private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键
     */
    private String memberCode;
    /**
     * 关联工作小组表-group_code
     */
    private String refGroupCode;
    /**
     * 关联签到记录id
     */
    private Long refSignRecordId;
    /**
     * 员工ERP|拼音|身份证号
     */
    private String userCode;
    /**
     * 签到人员名称
     */
    private String userName;
    /**
     * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工6-支援
     */
    private Integer jobCode;
    /**
     * 主操作人标识 1-是 0-否
     */
    private Integer masterFlag;
    /**
     * 状态 1-正常 0-退出
     */
    private Integer status;
    /**
     * 机构编码
     */
    private Integer orgCode;
    /**
     * 场地编码
     */
    private Integer siteCode;
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
     * 组员类型
     */
    private Integer memberType;
    /**
     * 设备类型编码
     */
    private String deviceTypeCode;
    /**
     * 设备类型名称
     */
    private String deviceTypeName;
    /**
     * 设备编码
     */
    private String machineCode;
	/**
	 * 加入时间
	 */
	private Date signInTime;

	/**
	 * 退出时间
	 */
	private Date signOutTime;
	/**
	 * 工作时长（单位：hh时mm分）
	 */
	private String workTimeStr;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getRefGroupCode() {
		return refGroupCode;
	}
	public void setRefGroupCode(String refGroupCode) {
		this.refGroupCode = refGroupCode;
	}
	public Long getRefSignRecordId() {
		return refSignRecordId;
	}
	public void setRefSignRecordId(Long refSignRecordId) {
		this.refSignRecordId = refSignRecordId;
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
	public Integer getJobCode() {
		return jobCode;
	}
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
	public Integer getMasterFlag() {
		return masterFlag;
	}
	public void setMasterFlag(Integer masterFlag) {
		this.masterFlag = masterFlag;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
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
	public Integer getMemberType() {
		return memberType;
	}
	public void setMemberType(Integer memberType) {
		this.memberType = memberType;
	}
	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}
	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}
	public String getDeviceTypeName() {
		return deviceTypeName;
	}
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
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
	public String getWorkTimeStr() {
		return workTimeStr;
	}
	public void setWorkTimeStr(String workTimeStr) {
		this.workTimeStr = workTimeStr;
	}
}
