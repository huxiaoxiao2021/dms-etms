package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/25 13:39
 * @Description: 返调度结果
 */
public class ReturnScheduleResult implements Serializable {

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 站点id
     */
    private Integer siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
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
}
