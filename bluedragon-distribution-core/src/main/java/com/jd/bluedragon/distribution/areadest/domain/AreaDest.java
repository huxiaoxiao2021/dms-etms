package com.jd.bluedragon.distribution.areadest.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 区域批次目的地
 * <p>
 * Created by lixin39 on 2016/12/7.
 */
public class AreaDest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private int id;

    /**
     * 始发分拣中心ID
     */
    private int createSiteCode;

    /**
     * 始发分拣中心名称
     */
    private String createSiteName;

    /**
     * 中转分拣中心ID
     */
    private int transferSiteCode;

    /**
     * 中转分拣中心名称
     */
    private String transferSiteName;

    /**
     * 批次目的地ID
     */
    private int receiveSiteCode;

    /**
     * 批次目的地名称
     */
    private String receiveSiteName;

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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(int createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public int getTransferSiteCode() {
        return transferSiteCode;
    }

    public void setTransferSiteCode(int transferSiteCode) {
        this.transferSiteCode = transferSiteCode;
    }

    public String getTransferSiteName() {
        return transferSiteName;
    }

    public void setTransferSiteName(String transferSiteName) {
        this.transferSiteName = transferSiteName;
    }

    public int getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(int receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
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
        return "AreaDest{" +
                "id=" + id +
                ", createSiteCode=" + createSiteCode +
                ", createSiteName='" + createSiteName + '\'' +
                ", transferSiteCode=" + transferSiteCode +
                ", transferSiteName='" + transferSiteName + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createUserCode=" + createUserCode +
                ", updateUser='" + updateUser + '\'' +
                ", updateUserCode=" + updateUserCode +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

}
