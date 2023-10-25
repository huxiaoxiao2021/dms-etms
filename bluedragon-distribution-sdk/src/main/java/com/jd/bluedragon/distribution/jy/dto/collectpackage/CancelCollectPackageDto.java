package com.jd.bluedragon.distribution.jy.dto.collectpackage;

import java.io.Serializable;
import java.util.Date;

public class CancelCollectPackageDto implements Serializable {


    private static final long serialVersionUID = -4635000191826670099L;
    private String bizId;
    private String boxCode;
    private String packageCode;
    private String updateUserName;
    private String updateUserErp;
    private Integer updateUserCode;
    private Integer siteCode;
    private String siteName;
    private Date updateTime;

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

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Integer updateUserCode) {
        this.updateUserCode = updateUserCode;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
