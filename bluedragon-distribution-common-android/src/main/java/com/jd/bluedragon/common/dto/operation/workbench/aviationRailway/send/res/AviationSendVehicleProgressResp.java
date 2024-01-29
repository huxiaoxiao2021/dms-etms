package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 发货任务详情 + 统计请求
 * @date 2023-08-17 14:05
 */
public class AviationSendVehicleProgressResp extends AviationSendTaskDto implements Serializable {

    /**
     * 已扫件数
     */
    private Integer scannedCount = 0;
    
    /**
     * 已扫包裹数
     */
    private Integer scannedPackCount = 0;

    /**
     * 已扫箱数
     */
    private Integer scannedBoxCount = 0;

    /**
     * 拦截包裹数
     */
    private Integer interceptedPackCount = 0;

    /**
     * 强制发包裹数
     */
    private Integer forceSendPackCount = 0;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 是否无任务；0-否 1-是
     */
    private Integer manualCreatedFlag;

    public Integer getScannedCount() {
        return scannedCount;
    }

    public void setScannedCount(Integer scannedCount) {
        this.scannedCount = scannedCount;
    }

    public Integer getScannedPackCount() {
        return scannedPackCount;
    }

    public void setScannedPackCount(Integer scannedPackCount) {
        this.scannedPackCount = scannedPackCount;
    }

    public Integer getScannedBoxCount() {
        return scannedBoxCount;
    }

    public void setScannedBoxCount(Integer scannedBoxCount) {
        this.scannedBoxCount = scannedBoxCount;
    }

    public Integer getInterceptedPackCount() {
        return interceptedPackCount;
    }

    public void setInterceptedPackCount(Integer interceptedPackCount) {
        this.interceptedPackCount = interceptedPackCount;
    }

    public Integer getForceSendPackCount() {
        return forceSendPackCount;
    }

    public void setForceSendPackCount(Integer forceSendPackCount) {
        this.forceSendPackCount = forceSendPackCount;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }
}
