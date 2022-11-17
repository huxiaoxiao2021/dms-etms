package com.jd.bluedragon.distribution.jy.group;


import java.io.Serializable;
import java.util.Date;

/**
 * 任务-小组人员明细表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:45:20
 */
public class JyTaskGroupMemberEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 关联任务表-id
     */
    private String refTaskId;
    /**
     * 关联工作任务表-group_code
     */
    private String refGroupCode;
    /**
     * 小组成员表-member_code
     */
    private String refGroupMemberCode;
    /**
     * 员工ERP|拼音|身份证号
     */
    private String userCode;
    /**
     * 员工名称
     */
    private String userName;
    /**
     * 员工工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工6-支援
     */
    private Integer jobCode;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 工作时长（秒）
     */
    private Integer workTimes;
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setRefTaskId(String refTaskId) {
        this.refTaskId = refTaskId;
    }

    public String getRefTaskId() {
        return this.refTaskId;
    }

    public String setRefGroupCode(String refGroupCode) {
        return this.refGroupCode = refGroupCode;
    }

    public String getRefGroupCode() {
        return this.refGroupCode;
    }

    public String setRefGroupMemberCode(String refGroupMemberCode) {
        return this.refGroupMemberCode = refGroupMemberCode;
    }

    public String getRefGroupMemberCode() {
        return this.refGroupMemberCode;
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

    public Date setStartTime(Date startTime) {
        return this.startTime = startTime;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public Date setEndTime(Date endTime) {
        return this.endTime = endTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public Integer setWorkTimes(Integer workTimes) {
        return this.workTimes = workTimes;
    }

    public Integer getWorkTimes() {
        return this.workTimes;
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

}
