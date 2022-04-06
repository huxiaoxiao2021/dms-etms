package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName UnloadCompleteVehicle
 * @Description
 * @Author wyh
 * @Date 2022/4/2 13:57
 **/
public class UnloadCompleteVehicle extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -7591490278976762291L;

    /**
     * 少扫件数
     */
    private Long lessCount;

    /**
     * 多扫件数
     */
    private Long moreCount;

    /**
     * 无任务卸车
     */
    private Boolean manualCreatedTask;

    /**
     * 是否异常
     */
    private Boolean abnormalFlag;

    /**
     * 卸车完成时间
     */
    private Date unloadFinishTime;

    /**
     * 任务主键
     */
    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getLessCount() {
        return lessCount;
    }

    public void setLessCount(Long lessCount) {
        this.lessCount = lessCount;
    }

    public Long getMoreCount() {
        return moreCount;
    }

    public void setMoreCount(Long moreCount) {
        this.moreCount = moreCount;
    }

    public Boolean getManualCreatedTask() {
        return manualCreatedTask;
    }

    public void setManualCreatedTask(Boolean manualCreatedTask) {
        this.manualCreatedTask = manualCreatedTask;
    }

    public Boolean getAbnormalFlag() {
        return abnormalFlag;
    }

    public void setAbnormalFlag(Boolean abnormalFlag) {
        this.abnormalFlag = abnormalFlag;
    }

    public Date getUnloadFinishTime() {
        return unloadFinishTime;
    }

    public void setUnloadFinishTime(Date unloadFinishTime) {
        this.unloadFinishTime = unloadFinishTime;
    }
}
