package com.jd.bluedragon.common.dto.operation.workbench.strand;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskTypeEnum;

import java.io.Serializable;

/**
 * 拣运app-滞留上报任务VO
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportTaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 场地编码
     */
    private Integer siteCode;
    
    /**
     * 波次时间
     */
    private String waveTime;

    /**
     * 下级流向场地编码
     */
    private Integer nextSiteCode;

    /**
     * 下级流向场地名称
     */
    private String nextSiteName;

    /**
     * 滞留原因编码
     */
    private Integer strandCode;

    /**
     * 滞留原因
     */
    private String strandReason;

    /**
     * 扫描数量
     */
    private Integer scanNum;

    /**
     * 任务状态
     * @see JyBizStrandTaskStatusEnum
     */
    private Integer taskStatus;

    /**
     * 任务类型
     * @see JyBizStrandTaskTypeEnum
     */
    private Integer taskType;

    /**
     * 服务器当前时间戳
     */
    private Long systemTime;
    
    /**
     * 任务终止时间戳
     */
    private Long taskEndTime;

    /**
     * 任务剩余时间戳
     */
    private Long taskRemainingTime;
    
    private Long obtainTime;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getWaveTime() {
        return waveTime;
    }

    public void setWaveTime(String waveTime) {
        this.waveTime = waveTime;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Integer getStrandCode() {
        return strandCode;
    }

    public void setStrandCode(Integer strandCode) {
        this.strandCode = strandCode;
    }

    public String getStrandReason() {
        return strandReason;
    }

    public void setStrandReason(String strandReason) {
        this.strandReason = strandReason;
    }

    public Integer getScanNum() {
        return scanNum;
    }

    public void setScanNum(Integer scanNum) {
        this.scanNum = scanNum;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(Long systemTime) {
        this.systemTime = systemTime;
    }

    public Long getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Long taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public Long getTaskRemainingTime() {
        return taskRemainingTime;
    }

    public void setTaskRemainingTime(Long taskRemainingTime) {
        this.taskRemainingTime = taskRemainingTime;
    }

    public Long getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime(Long obtainTime) {
        this.obtainTime = obtainTime;
    }
}
