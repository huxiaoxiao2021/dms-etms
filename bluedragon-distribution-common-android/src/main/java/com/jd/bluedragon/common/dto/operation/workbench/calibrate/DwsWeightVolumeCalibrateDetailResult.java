package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import java.io.Serializable;
import java.util.List;

/**
 * 设备称重量方校准明细返回体
 *
 * @author hujiping
 * @date 2022/12/15 10:29 AM
 */
public class DwsWeightVolumeCalibrateDetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     */
    private String machineCode;

    /**
     * 设备状态
     */
    private Integer machineStatus;

    /**
     * 重量体积校验详情
     */
    private List<DwsWeightVolumeCalibrateDetail> detailList;

    /**
     * 设备任务开始时间
     */
    private Long taskCreateTime;

    /**
     * 设备任务结束时间
     */
    private Long taskEndTime;

    /**
     * 设备任务完成时间
     */
    private Long calibrateFinishTime;

    /**
     * 设备上次合格时间
     */
    private Long previousMachineEligibleTime;

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

    public List<DwsWeightVolumeCalibrateDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DwsWeightVolumeCalibrateDetail> detailList) {
        this.detailList = detailList;
    }

    public Long getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(Long taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }

    public Long getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Long taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public Long getCalibrateFinishTime() {
        return calibrateFinishTime;
    }

    public void setCalibrateFinishTime(Long calibrateFinishTime) {
        this.calibrateFinishTime = calibrateFinishTime;
    }

    public Long getPreviousMachineEligibleTime() {
        return previousMachineEligibleTime;
    }

    public void setPreviousMachineEligibleTime(Long previousMachineEligibleTime) {
        this.previousMachineEligibleTime = previousMachineEligibleTime;
    }
}
