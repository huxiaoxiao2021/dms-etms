package com.jd.bluedragon.common.dto.group;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: GroupMemberRequest
 * @Description: 小组人员请求
 * @author wuyoude
 * @date 2022年03月30日 11:01:53
 *
 */
public class GroupMemberRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private Long id;
	/**
	 * 小组编码
	 */
	private String groupCode;
	/**
	 * 签到记录Id
	 */
	private Long signRecordId;
	/**
	 * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工
	 */
	private Integer jobCode;    
	/**
	 * 员工ERP|拼音|身份证号
	 */
	private String userCode;
	/**
	 * 员工名称
	 */
	private String userName;
	/**
	 * 扫描的三定人员编码
	 */
	private String scanUserCode;
    /**
     * 岗位编码
     */
    private String positionCode;
	/**
	 * 机构编码
	 */
	private Integer orgCode;
	/**
	 * 场地编码
	 */
	private Integer siteCode;
	
	/**
	 * 操作人code
	 */
	private String operateUserCode;
	/**
	 * 操作人name
	 */
	private String operateUserName;
    /**
     * 业务主键
     */
    private String memberCode;
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
	 * 签到记录Id列表
	 */
	private List<Long> signRecordIdList;
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public Long getSignRecordId() {
		return signRecordId;
	}
	public void setSignRecordId(Long signRecordId) {
		this.signRecordId = signRecordId;
	}
	public Integer getJobCode() {
		return jobCode;
	}
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
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
	public String getScanUserCode() {
		return scanUserCode;
	}
	public void setScanUserCode(String scanUserCode) {
		this.scanUserCode = scanUserCode;
	}
	public String getPositionCode() {
		return positionCode;
	}
	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
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
	public String getOperateUserCode() {
		return operateUserCode;
	}
	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}
	public String getOperateUserName() {
		return operateUserName;
	}
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public List<Long> getSignRecordIdList() {
		return signRecordIdList;
	}
	public void setSignRecordIdList(List<Long> signRecordIdList) {
		this.signRecordIdList = signRecordIdList;
	}
}
