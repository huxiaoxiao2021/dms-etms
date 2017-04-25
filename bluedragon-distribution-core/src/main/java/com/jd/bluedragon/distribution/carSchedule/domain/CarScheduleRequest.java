package com.jd.bluedragon.distribution.carSchedule.domain;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2017/4/17.
 */
public class CarScheduleRequest implements Serializable{

    private static final long serialVersionUID = -4347695111783258037L;

    private String vehicleNumber;

    private String siteCode;

    private Integer key;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }
}
