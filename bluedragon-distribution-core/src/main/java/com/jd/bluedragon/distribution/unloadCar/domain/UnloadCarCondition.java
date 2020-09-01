package com.jd.bluedragon.distribution.unloadCar.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2020/6/23 20:52
 */
public class UnloadCarCondition extends BasePagerCondition {

    private Date startTime;

    private Date endTime;

    private String vehicleNumber;

    private Integer distributeType;

    private List<Integer> status;

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

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getDistributeType() {
        return distributeType;
    }

    public void setDistributeType(Integer distributeType) {
        this.distributeType = distributeType;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }
}
