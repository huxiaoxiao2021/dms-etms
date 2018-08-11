package com.jd.bluedragon.distribution.siteRetake.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 驻厂再取 查询类
 * @date 2018年08月02日 16时:29分
 */
public class SiteRetakeCondition {
    public static final Integer TIME_TYPE_ASSIGNTIME=0;
    public static final Integer TIME_TYPE_ORDERTIME=1;

    private Date selectTime;
    private Integer VendorId;
    private Integer siteCode;
    private Integer  timeType;

    public Date getSelectTime() {
        return selectTime;
    }

    public void setSelectTime(Date selectTime) {
        this.selectTime = selectTime;
    }

    public Integer getVendorId() {
        return VendorId;
    }

    public void setVendorId(Integer vendorId) {
        VendorId = vendorId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }
}
