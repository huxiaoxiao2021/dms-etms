package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hanjiaxing1 on 2016/12/16.
 */
public class GantryExceptionRequest implements Serializable {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 分拣中心编号
     */
    private Integer siteCode;

    /**
     * 龙门架设备编号
     */
    private String machineId;

    /**
     * 失败类型
     */
    private Integer type;

    /**
     * 发货状态
     */
    private Integer sendStatus;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 开始时间
     */
    private String endTime;

    /**
     * 业务类型：1 :龙门架 2：自动分拣机
     * @return
     */
    private Integer busiType = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Integer getBusiType() {
        return busiType;
    }

    public void setBusiType(Integer busiType) {
        this.busiType = busiType;
    }
}
