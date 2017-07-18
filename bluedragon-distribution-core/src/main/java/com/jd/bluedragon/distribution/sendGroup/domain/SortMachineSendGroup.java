package com.jd.bluedragon.distribution.sendGroup.domain;

import java.util.Date;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
public class SortMachineSendGroup {
    /** 全局唯一ID */
    private Long id;
    /*分组名称*/
    private String groupName;
    /*分拣机代码*/
    private String machineCode;
    /*创建人*/
    private String createUser;
    /*创建人编码*/
    private Long createUserCode;
    /*创建时间*/
    private Date createTime;
    /*修改用户编码*/
    private Long updateUserCode;
    /*修改用户*/
    private String updateUser;
    /*修改时间*/
    private Date updateTime;
    /*是否删除,0-删除,1-使用*/
    private Integer yn;
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Long createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Long updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
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
}
