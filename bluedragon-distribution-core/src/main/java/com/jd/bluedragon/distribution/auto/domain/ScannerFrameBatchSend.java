package com.jd.bluedragon.distribution.auto.domain;

import com.jd.bluedragon.utils.JsonHelper;

import java.io.Serializable;
import java.util.*;
/**
 * 龙门驾批量发货
 * Created by wangtingwei on 2016/12/8.
 */
public class ScannerFrameBatchSend implements Serializable {

    private static final long serialVersionUID = 42L;

    /**
     * 主键
     */
    private long id;

    /**
     * 龙门架注册号
     */
    private long machineId;

    /**
     * 发货站点Id
     */
    private long createSiteCode;

    /**
     * 发货站点名称
     */
    private String createSiteName;

    /**
     * 接收站点Id
     */
    private long receiveSiteCode;

    /**
     * 接收站点名称
     */
    private String receiveSiteName;

    /**
     * 发货批次号
     */
    private String sendCode;

    /**
     * 交接单打印次数
     */
    private byte printTimes;

    /**
     * 记录创建用户Id
     */
    private long createUserCode;

    /**
     * 记录创建用户姓名
     */
    private String createUserName;

    /**
     * 记录更新用户Id
     */
    private Long updateUserCode;

    /**
     * 记录更新用户姓名
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
     * 最后一次打印时间
     */
    private Date lastPrintTime;

    /**
     * 标识位
     */
    private byte yn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public long getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(long receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public byte getPrintTimes() {
        return printTimes;
    }

    public void setPrintTimes(byte printTimes) {
        this.printTimes = printTimes;
    }

    public long getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(long createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Long getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Long updateUserCode) {
        this.updateUserCode = updateUserCode;
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

    public byte getYn() {
        return yn;
    }

    public void setYn(byte yn) {
        this.yn = yn;
    }

    public Date getLastPrintTime() {
        return lastPrintTime;
    }

    public void setLastPrintTime(Date lastPrintTime) {
        this.lastPrintTime = lastPrintTime;
    }

    @Override
    public String toString(){
        return JsonHelper.toJson(this);
    }

}
