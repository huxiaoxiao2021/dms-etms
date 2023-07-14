package com.jd.bluedragon.common.dto.inventory;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:28
 * @Description  找货任务
 */
public class InventoryTaskDto implements Serializable {

    private static final long serialVersionUID = -6051001368608203945L;


    private String bizId;
    /**
     * 波次起止时间
     */
    private Long waveStartTime;
    private Long waveEndTime;
    /**
     * 倒计时秒：
     * 计划完成时间-当前时间的秒数
     * 计划完成时间=（波次结束时间+30min(配置延迟时间)）
     */
    private Long countdownSeconds;
    /**
     * 任务状态
     */
    private Integer taskStatus;
    /**
     * 待找货数量
     */
    private Integer waitFindCount;
    /**
     * 已找货数量
     */
    private Integer haveFindCount;
    /**
     * 拍照方位:如123
     */
    private String photoStatus;
    /**
     * 拍照数量
     */
    private Integer photoCount;
    /**
     * 应拍照总数
     */
    private Integer photoTotalCount;
    /**
     * 关闭时间(波次结束时间+30min(配置延迟时间))
     */
    private Long completeTime;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getWaveStartTime() {
        return waveStartTime;
    }

    public void setWaveStartTime(Long waveStartTime) {
        this.waveStartTime = waveStartTime;
    }

    public Long getWaveEndTime() {
        return waveEndTime;
    }

    public void setWaveEndTime(Long waveEndTime) {
        this.waveEndTime = waveEndTime;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getWaitFindCount() {
        return waitFindCount;
    }

    public void setWaitFindCount(Integer waitFindCount) {
        this.waitFindCount = waitFindCount;
    }

    public Integer getHaveFindCount() {
        return haveFindCount;
    }

    public void setHaveFindCount(Integer haveFindCount) {
        this.haveFindCount = haveFindCount;
    }

    public String getPhotoStatus() {
        return photoStatus;
    }

    public void setPhotoStatus(String photoStatus) {
        this.photoStatus = photoStatus;
    }

    public Long getCountdownSeconds() {
        return countdownSeconds;
    }

    public void setCountdownSeconds(Long countdownSeconds) {
        this.countdownSeconds = countdownSeconds;
    }

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public Integer getPhotoTotalCount() {
        return photoTotalCount;
    }

    public void setPhotoTotalCount(Integer photoTotalCount) {
        this.photoTotalCount = photoTotalCount;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }
}
