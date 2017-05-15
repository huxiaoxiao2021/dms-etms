package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.Date;

/**
 *
 */
public class GantryVelocityRequest extends JdRequest {
    /**
     * 龙门架编号全国维一编号
     */
    private Integer machineId;

    /**
    *操作人所属站点
    */
    private Integer createSiteCode;

    /*
    龙门架序列号,
     */
    private String gantrySerialNumber;

    /*
    卸货开始时间
     */
    private Date startTime;

    /*
   计算结束
    */
    private Date endTime;

    private Integer yn;

    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getGantrySerialNumber() {
        return gantrySerialNumber;
    }

    public void setGantrySerialNumber(String gantrySerialNumber) {
        this.gantrySerialNumber = gantrySerialNumber;
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

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
