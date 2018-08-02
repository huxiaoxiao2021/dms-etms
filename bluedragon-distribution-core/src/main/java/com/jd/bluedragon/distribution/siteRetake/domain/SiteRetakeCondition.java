package com.jd.bluedragon.distribution.siteRetake.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年08月02日 16时:29分
 */
public class SiteRetakeCondition {
    private Date startTime;
    private Date endTime;
    private Integer traderId;
    private String siteCode;
    private String userErp;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTraderId() {
        return traderId;
    }

    public void setTraderId(Integer traderId) {
        this.traderId = traderId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
