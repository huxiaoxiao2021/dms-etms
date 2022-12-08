package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import com.jd.bluedragon.common.dto.operation.workbench.enums.MachineCalibrateStatus;

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
     * @see MachineCalibrateStatus
     */
    private Integer machineCalibrateStatus;
    /**
     * 任务状态
     */
    private Integer taskStatus;
    /**
     * 校准结果
     */
    private Integer calibrateStatus;
    /**
     * 校准任务开始时间
     */
    private Date taskStartTime;
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

    public Integer getMachineCalibrateStatus() {
        return machineCalibrateStatus;
    }

    public void setMachineCalibrateStatus(Integer machineCalibrateStatus) {
        this.machineCalibrateStatus = machineCalibrateStatus;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getCalibrateStatus() {
        return calibrateStatus;
    }

    public void setCalibrateStatus(Integer calibrateStatus) {
        this.calibrateStatus = calibrateStatus;
    }

    public Date getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
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
