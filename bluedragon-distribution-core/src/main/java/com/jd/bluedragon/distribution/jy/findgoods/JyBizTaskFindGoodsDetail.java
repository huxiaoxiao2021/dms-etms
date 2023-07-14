package com.jd.bluedragon.distribution.jy.findgoods;

import java.util.Date;

public class JyBizTaskFindGoodsDetail {
    private Long id;

    private String findGoodsTaskBizId;

    private String packageCode;

    private Long siteCode;

    private String waveStartTime;

    private String waveEndTime;

    private Byte findStatus;

    private Byte findType;

    private String findUserErp;

    private String findUserName;

    private String lastFindGridKey;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean yn;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFindGoodsTaskBizId() {
        return findGoodsTaskBizId;
    }

    public void setFindGoodsTaskBizId(String findGoodsTaskBizId) {
        this.findGoodsTaskBizId = findGoodsTaskBizId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getWaveStartTime() {
        return waveStartTime;
    }

    public void setWaveStartTime(String waveStartTime) {
        this.waveStartTime = waveStartTime;
    }

    public String getWaveEndTime() {
        return waveEndTime;
    }

    public void setWaveEndTime(String waveEndTime) {
        this.waveEndTime = waveEndTime;
    }

    public Byte getFindStatus() {
        return findStatus;
    }

    public void setFindStatus(Byte findStatus) {
        this.findStatus = findStatus;
    }

    public Byte getFindType() {
        return findType;
    }

    public void setFindType(Byte findType) {
        this.findType = findType;
    }

    public String getFindUserErp() {
        return findUserErp;
    }

    public void setFindUserErp(String findUserErp) {
        this.findUserErp = findUserErp;
    }

    public String getFindUserName() {
        return findUserName;
    }

    public void setFindUserName(String findUserName) {
        this.findUserName = findUserName;
    }

    public String getLastFindGridKey() {
        return lastFindGridKey;
    }

    public void setLastFindGridKey(String lastFindGridKey) {
        this.lastFindGridKey = lastFindGridKey;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
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

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}