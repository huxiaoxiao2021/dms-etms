package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName UnloadVehicleInfo
 * @Description
 * @Author wyh
 * @Date 2022/4/2 11:34
 **/
public class UnloadVehicleInfo extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = 7007964985586446559L;

    /**
     * 卸车进度
     */
    private BigDecimal unloadProgress;

    /**
     * 单号标签集合
     */
    private List<LabelOption> tags;

    /**
     * 无任务卸车
     */
    private Boolean manualCreatedTask;

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

    public BigDecimal getUnloadProgress() {
        return unloadProgress;
    }

    public void setUnloadProgress(BigDecimal unloadProgress) {
        this.unloadProgress = unloadProgress;
    }

    public List<LabelOption> getTags() {
        return tags;
    }

    public void setTags(List<LabelOption> tags) {
        this.tags = tags;
    }

    public Boolean getManualCreatedTask() {
        return manualCreatedTask;
    }

    public void setManualCreatedTask(Boolean manualCreatedTask) {
        this.manualCreatedTask = manualCreatedTask;
    }

    private Boolean _active;

    public Boolean get_active() {
        return _active;
    }

    public void set_active(Boolean _active) {
        this._active = _active;
    }
}
