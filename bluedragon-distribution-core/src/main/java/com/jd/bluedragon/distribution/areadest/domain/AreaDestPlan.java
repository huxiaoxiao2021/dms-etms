package com.jd.bluedragon.distribution.areadest.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 龙门架发货关系方案信息表
 * <p>
 * Created by lixin39 on 2017/3/14.
 */
public class AreaDestPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 方案id
     */
    private Integer planId;

    /**
     * 方案名称
     */
    private String planName;

    /**
     * 龙门架id
     */
    private Integer machineId;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     * 创建用户编号
     */
    private int createUserCode;

    /**
     * 修改用户
     */
    private String updateUser;

    /**
     * 修改用户编号
     */
    private int updateUserCode;

    /**
     * 添加时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public int getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(int createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public int getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(int updateUserCode) {
        this.updateUserCode = updateUserCode;
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

    @Override
    public String toString() {
        return "AreaDestPlan{" +
                "planId=" + planId +
                ", planName='" + planName + '\'' +
                ", machineId=" + machineId +
                ", operateSiteCode=" + operateSiteCode +
                ", createUser='" + createUser + '\'' +
                ", createUserCode=" + createUserCode +
                ", updateUser='" + updateUser + '\'' +
                ", updateUserCode=" + updateUserCode +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
