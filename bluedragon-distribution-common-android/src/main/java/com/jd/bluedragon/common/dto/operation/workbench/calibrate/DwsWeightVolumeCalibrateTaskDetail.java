package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateHintEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * dws设备称重量方校准任务详情
 *
 * @author hujiping
 * @date 2022/12/7 8:17 PM
 */
public class DwsWeightVolumeCalibrateTaskDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    private String machineCode;
    /**
     * 设备校准状态
     * @see JyBizTaskMachineCalibrateStatusEnum
     */
    private Integer machineStatus;
    /**
     * 任务状态
     */
    private Integer taskStatus;
    /**
     * 校准提示
     * @see JyBizTaskMachineCalibrateHintEnum
     */
    private Integer calibrateHint;
    /**
     * 校准任务开始时间
     */
    private Date taskCreateTime;
    /**
     * 校准任务截止时间
     */
    private Date taskEndTime;
    /**
     * 设备校准完成时间
     */
    private Date calibrateFinishTime;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Integer getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(Integer machineStatus) {
        this.machineStatus = machineStatus;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getCalibrateHint() {
        return calibrateHint;
    }

    public void setCalibrateHint(Integer calibrateHint) {
        this.calibrateHint = calibrateHint;
    }

    public Date getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Date taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public Date getCalibrateFinishTime() {
        return calibrateFinishTime;
    }

    public void setCalibrateFinishTime(Date calibrateFinishTime) {
        this.calibrateFinishTime = calibrateFinishTime;
    }
}
