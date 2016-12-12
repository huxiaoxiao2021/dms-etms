package com.jd.bluedragon.distribution.auto.domain;

import java.io.Serializable;
import java.util.*;

/**
 * Created by wangtingwei on 2016/12/12.
 */
public class ScannerFrameBatchSendSearchArgument implements Serializable {
    private static final long serialVersionUID=1L;

    /**
     * 龙门架编号
     */
    private long machineId;

    /**
     * 批次创建开始时间
     */
    private Date startTime;

    /**
     * 批次创建结束时间
     */
    private Date endTime;

    /**
     * 收货站点
     */
    private Integer receiveSiteCode;

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
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

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
}
