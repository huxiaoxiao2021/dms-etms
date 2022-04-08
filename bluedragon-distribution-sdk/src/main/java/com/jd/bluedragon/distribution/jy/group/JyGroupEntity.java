package com.jd.bluedragon.distribution.jy.group;


import java.io.Serializable;
import java.util.Date;

/**
 * 工作小组表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 15:45:20
 */
public class JyGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 组编码
     */
    private String groupCode;
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

    public Long setId(Long id) {
        return this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String setGroupCode(String groupCode) {
        return this.groupCode = groupCode;
    }

    public String getGroupCode() {
        return this.groupCode;
    }

    public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
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

}
