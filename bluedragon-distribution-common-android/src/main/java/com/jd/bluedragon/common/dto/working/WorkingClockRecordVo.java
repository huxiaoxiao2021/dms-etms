package com.jd.bluedragon.common.dto.working;

import java.io.Serializable;

public class WorkingClockRecordVo implements Serializable {

    private Long id;

    /**
     * 计件签到时间
     */
    private String sinInTime;

    private String sinInDate;

    /**
     * 计件签退时间
     */
    private String sinOutTime;

    private String sinOutDate;

    /**
     * 员工erp
     */
    private String employeeErp;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 支援工时
     */
    private String clockHours;

    /**
     * 人资考勤工时
     */
    private String hrWorkingHours;

    /**
     * 支援小组ID
     */
    private String clockGroupId;

    /**
     * 跨天
     */
    private String crossDays;

    /**
     * 支援
     */
    private String outSupport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSinInTime() {
        return sinInTime;
    }

    public void setSinInTime(String sinInTime) {
        this.sinInTime = sinInTime;
    }

    public String getSinInDate() {
        return sinInDate;
    }

    public void setSinInDate(String sinInDate) {
        this.sinInDate = sinInDate;
    }

    public String getSinOutTime() {
        return sinOutTime;
    }

    public void setSinOutTime(String sinOutTime) {
        this.sinOutTime = sinOutTime;
    }

    public String getSinOutDate() {
        return sinOutDate;
    }

    public void setSinOutDate(String sinOutDate) {
        this.sinOutDate = sinOutDate;
    }

    public String getEmployeeErp() {
        return employeeErp;
    }

    public void setEmployeeErp(String employeeErp) {
        this.employeeErp = employeeErp;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getClockHours() {
        return clockHours;
    }

    public void setClockHours(String clockHours) {
        this.clockHours = clockHours;
    }

    public String getHrWorkingHours() {
        return hrWorkingHours;
    }

    public void setHrWorkingHours(String hrWorkingHours) {
        this.hrWorkingHours = hrWorkingHours;
    }

    public String getClockGroupId() {
        return clockGroupId;
    }

    public void setClockGroupId(String clockGroupId) {
        this.clockGroupId = clockGroupId;
    }

    public String getCrossDays() {
        return crossDays;
    }

    public void setCrossDays(String crossDays) {
        this.crossDays = crossDays;
    }

    public String getOutSupport() {
        return outSupport;
    }

    public void setOutSupport(String outSupport) {
        this.outSupport = outSupport;
    }
}
