package com.jd.bluedragon.distribution.jy.group;


import java.io.Serializable;
import java.util.Date;

/**
 * 组成员表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:45:20
 */
public class JyGroupMemberEntity implements Serializable {
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

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setMemberCode(String memberCode) {
        return this.memberCode = memberCode;
    }

    public String getMemberCode() {
        return this.memberCode;
    }

    public String setRefGroupCode(String refGroupCode) {
        return this.refGroupCode = refGroupCode;
    }

    public String getRefGroupCode() {
        return this.refGroupCode;
    }

    public Long setRefSignRecordId(Long refSignRecordId) {
        return this.refSignRecordId = refSignRecordId;
    }

    public Long getRefSignRecordId() {
        return this.refSignRecordId;
    }

    public String setUserCode(String userCode) {
        return this.userCode = userCode;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public String setUserName(String userName) {
        return this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public Integer setJobCode(Integer jobCode) {
        return this.jobCode = jobCode;
    }

    public Integer getJobCode() {
        return this.jobCode;
    }

    public Integer setMasterFlag(Integer masterFlag) {
        return this.masterFlag = masterFlag;
    }

    public Integer getMasterFlag() {
        return this.masterFlag;
    }

    public Integer setStatus(Integer status) {
        return this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public Integer setOrgCode(Integer orgCode) {
        return this.orgCode = orgCode;
    }

    public Integer getOrgCode() {
        return this.orgCode;
    }

    public Integer setSiteCode(Integer siteCode) {
        return this.siteCode = siteCode;
    }

    public Integer getSiteCode() {
        return this.siteCode;
    }

    public String setCreateUser(String createUser) {
        return this.createUser = createUser;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public String setCreateUserName(String createUserName) {
        return this.createUserName = createUserName;
    }

    public String getCreateUserName() {
        return this.createUserName;
    }

    public String setUpdateUser(String updateUser) {
        return this.updateUser = updateUser;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public String setUpdateUserName(String updateUserName) {
        return this.updateUserName = updateUserName;
    }

    public String getUpdateUserName() {
        return this.updateUserName;
    }

    public Date setCreateTime(Date createTime) {
        return this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public Date setUpdateTime(Date updateTime) {
        return this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public Integer setYn(Integer yn) {
        return this.yn = yn;
    }

    public Integer getYn() {
        return this.yn;
    }

    public Date setTs(Date ts) {
        return this.ts = ts;
    }

    public Date getTs() {
        return this.ts;
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

}
