package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.Date;

/**
 * Created by jinjingcheng on 2017/7/11.
 */
public class SendExceptionRequest extends JdRequest{
    /*
    龙门架/分拣机编号
     */
    private String machineId;
    /*
    批次号开始时间
     */
    private Date startTime;

    /*
    批次号结束时间
     */
    private Date endTime;

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

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
}
