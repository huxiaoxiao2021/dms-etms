package com.jd.bluedragon.distribution.schedule.vo;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/12 17:15
 */
public class DmsEdnPickingExportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 调度单号
     */
    private String scheduleBillCode;

    /**
     * 承运商名称
     */
    private String carrierName;

    /**
     * 调度时间，调度信息变更以此时间为准
     */
    private String scheduleTime;

    public String getScheduleBillCode() {
        return scheduleBillCode;
    }

    public void setScheduleBillCode(String scheduleBillCode) {
        this.scheduleBillCode = scheduleBillCode;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }
}
    
