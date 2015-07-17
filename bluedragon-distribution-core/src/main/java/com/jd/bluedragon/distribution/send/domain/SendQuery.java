package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.Date;
/**
 * 发货批次查询记录
 * Created by wangtingwei on 2014/12/5.
 */
public class SendQuery implements Serializable {

    /**
     * 记录ID
     */
    private long id;

    /**
     * 发货批次号
     */
    private String sendCode;

    /**
     * 创建站点ID
     */
    private int createSiteCode;

    /**
     * 创建用户真实姓名
     */
    private String createUserRealName;

    /**
     * 创建用户编号
     */
    private int createUserCode;

    /**
     * 更新用户真实姓名
     */
    private String updateUserRealName;

    /**
     * 更新用户ID
     */
    private Integer updateUserCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 标识列
     */
    private int yn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public int getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(int createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateUserRealName() {
        return createUserRealName;
    }

    public void setCreateUserRealName(String createUserRealName) {
        this.createUserRealName = createUserRealName;
    }

    public int getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(int createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getUpdateUserRealName() {
        return updateUserRealName;
    }

    public void setUpdateUserRealName(String updateUserRealName) {
        this.updateUserRealName = updateUserRealName;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
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

    public int getYn() {
        return yn;
    }

    public void setYn(int yn) {
        this.yn = yn;
    }
}
