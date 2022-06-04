package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BaseSendVehicle
 * @Description
 * @Author wyh
 * @Date 2022/5/18 18:23
 **/
public class BaseSendVehicle implements Serializable {

    private static final long serialVersionUID = -5005890642092421853L;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 是否是自建任务 true：自建
     */
    private Boolean manualCreatedFlag;

    /**
     * 无任务是否绑定了任务 true：绑定
     */
    private Boolean noTaskBindVehicle;

    /**
     * 发货任务业务主键
     */
    private String sendVehicleBizId;

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 运输派车单编码
     */
    private String transWorkCode;

    /**
     * 任务标签集合
     */
    private List<LabelOption> tags;

    private Boolean _active;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Boolean getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Boolean manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public Boolean getNoTaskBindVehicle() {
        return noTaskBindVehicle;
    }

    public void setNoTaskBindVehicle(Boolean noTaskBindVehicle) {
        this.noTaskBindVehicle = noTaskBindVehicle;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

    public List<LabelOption> getTags() {
        return tags;
    }

    public void setTags(List<LabelOption> tags) {
        this.tags = tags;
    }

    public Boolean get_active() {
        return _active;
    }

    public void set_active(Boolean _active) {
        this._active = _active;
    }
}
