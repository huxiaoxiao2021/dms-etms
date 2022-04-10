package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ToUnloadVehicle
 * @Description
 * @Author wyh
 * @Date 2022/4/2 11:32
 **/
public class ToUnloadVehicle extends VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -6185166839228690016L;

    /**
     * 解封车时间
     */
    private Date deSealCarTime;

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

    public Date getDeSealCarTime() {
        return deSealCarTime;
    }

    public void setDeSealCarTime(Date deSealCarTime) {
        this.deSealCarTime = deSealCarTime;
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
