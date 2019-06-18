package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;
import java.util.Date;

public class CurrentOperate implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
    操作单位编号
     */
    public int siteCode;
    /*
    操作单位名称
     */
    public String siteName;
    /*
    操作时间
     */
    public Date operateTime;


    public CurrentOperate() {
    }

    public CurrentOperate(int siteCode, String siteName, Date operateTime) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.operateTime = operateTime;
    }

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

}
