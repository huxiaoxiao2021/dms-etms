package com.jd.bluedragon.distribution.worker.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.sql.Date;

/**
 * 淘宝调度任务
 * Created by wangtingwei on 2015/10/8.
 */
public class TBTaskType {
    private int id;
    private String baseTaskType;
    private long heartBeatRate = 60000L;
    private long judgeDeadInterval = 120000L;
    private int sleepTimeNoData = 500;
    private int sleepTimeInterval = 0;
    private int taskQueueNumber = -1;
    private int fetchDataNumber = 100;
    private int executeNumber = 1;
    private int threadNumber = 1;
    private String processorType;
    private String permitRunStartTime;
    private String permitRunEndTime;
    private double expireOwnSignInterval;
    private String dealBeanName;
    private long version;
    private Date gmtCreate;
    private Date gmtModified;
    private Date lastAssignTime;
    private String lastAssignUuid;
    public TBTaskType() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getLastAssignTime() {
        return lastAssignTime;
    }

    public void setLastAssignTime(Date lastAssignTime) {
        this.lastAssignTime = lastAssignTime;
    }

    public String getLastAssignUuid() {
        return lastAssignUuid;
    }

    public void setLastAssignUuid(String lastAssignUuid) {
        this.lastAssignUuid = lastAssignUuid;
    }

    public int getTaskQueueNumber() {
        return this.taskQueueNumber;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getBaseTaskType() {
        return this.baseTaskType;
    }

    public void setBaseTaskType(String baseTaskType) {
        this.baseTaskType = baseTaskType;
    }

    public void setTaskQueueNumber(int taskQueueNumber) {
        this.taskQueueNumber = taskQueueNumber;
    }

    public long getHeartBeatRate() {
        return this.heartBeatRate;
    }

    public void setHeartBeatRate(long heartBeatRate) {
        this.heartBeatRate = heartBeatRate;
    }

    public long getJudgeDeadInterval() {
        return this.judgeDeadInterval;
    }

    public void setJudgeDeadInterval(long judgeDeadInterval) {
        this.judgeDeadInterval = judgeDeadInterval;
    }

    public int getFetchDataNumber() {
        return this.fetchDataNumber;
    }

    public void setFetchDataNumber(int fetchDataNumber) {
        this.fetchDataNumber = fetchDataNumber;
    }

    public int getExecuteNumber() {
        return this.executeNumber;
    }

    public void setExecuteNumber(int executeNumber) {
        this.executeNumber = executeNumber;
    }

    public int getSleepTimeNoData() {
        return this.sleepTimeNoData;
    }

    public void setSleepTimeNoData(int sleepTimeNoData) {
        this.sleepTimeNoData = sleepTimeNoData;
    }

    public int getSleepTimeInterval() {
        return this.sleepTimeInterval;
    }

    public void setSleepTimeInterval(int sleepTimeInterval) {
        this.sleepTimeInterval = sleepTimeInterval;
    }

    public int getThreadNumber() {
        return this.threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public String getPermitRunStartTime() {
        return this.permitRunStartTime;
    }

    public String getProcessorType() {
        return this.processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    public void setPermitRunStartTime(String permitRunStartTime) {
        this.permitRunStartTime = permitRunStartTime;
        if(this.permitRunStartTime != null && this.permitRunStartTime.trim().length() == 0) {
            this.permitRunStartTime = null;
        }

    }

    public String getPermitRunEndTime() {
        return this.permitRunEndTime;
    }

    public double getExpireOwnSignInterval() {
        return this.expireOwnSignInterval;
    }

    public void setExpireOwnSignInterval(double expireOwnSignInterval) {
        this.expireOwnSignInterval = expireOwnSignInterval;
    }

    public String getDealBeanName() {
        return this.dealBeanName;
    }

    public void setDealBeanName(String dealBeanName) {
        this.dealBeanName = dealBeanName;
    }

    public void setPermitRunEndTime(String permitRunEndTime) {
        this.permitRunEndTime = permitRunEndTime;
        this.permitRunEndTime = permitRunEndTime;
        if(this.permitRunEndTime != null && this.permitRunEndTime.trim().length() == 0) {
            this.permitRunEndTime = null;
        }

    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}