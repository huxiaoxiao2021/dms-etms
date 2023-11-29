package com.jd.bluedragon.distribution.jy.collectpackage;

import java.util.Date;

public class JyCollectPackageEntity {
    private Long id;

    private String bizId;

    private String boxCode;

    private String packageCode;

    private Long startSiteId;

    private String startSiteName;

    private Long endSiteId;

    private String endSiteName;

    private Long boxEndSiteId;

    private String boxEndSiteName;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Boolean interceptFlag;

    private Boolean forceFlag;

    private Boolean yn;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getBoxEndSiteId() {
        return boxEndSiteId;
    }

    public void setBoxEndSiteId(Long boxEndSiteId) {
        this.boxEndSiteId = boxEndSiteId;
    }

    public String getBoxEndSiteName() {
        return boxEndSiteName;
    }

    public void setBoxEndSiteName(String boxEndSiteName) {
        this.boxEndSiteName = boxEndSiteName;
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

    public Boolean getInterceptFlag() {
        return interceptFlag;
    }

    public void setInterceptFlag(Boolean interceptFlag) {
        this.interceptFlag = interceptFlag;
    }

    public Boolean getForceFlag() {
        return forceFlag;
    }

    public void setForceFlag(Boolean forceFlag) {
        this.forceFlag = forceFlag;
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