package com.jd.bluedragon.common.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class JobCodeHoursDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer jobCode;

    private Integer hour;

    private List<Integer> jobCodes;
    private Date startTime;

    private Date endTime;


    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getJobCode() {
        return jobCode;
    }

    public void setJobCode(Integer jobCode) {
        this.jobCode = jobCode;
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

    public List<Integer> getJobCodes() {
        return jobCodes;
    }

    public void setJobCodes(List<Integer> jobCodes) {
        this.jobCodes = jobCodes;
    }
}
