package com.jd.bluedragon.distribution.jy.dto.calibrate;

import java.util.Date;

public class JyBizTaskMachineCalibrateQuery {

    /**
     * 设备编码
     */
    private String machineCode;

    /**
     * 操作校准重量或者体积的时间
     */
    private Date calibrateTime;

    /**
     * 任务创建时间
     */
    private Date taskCreateTime;
    /**
     * 任务截止时间
     */
    private Date taskEndTime;
    /**
     * 任务状态:0-待处理,1-已完成,2-超时,3-关闭
     */
    private Integer taskStatus;
    /**
     * 重量校准时间
     */
    private Date weightCalibrateTime;
    /**
     * 体积校准时间
     */
    private Date volumeCalibrateTime;
    /**
     * 校准完成时间
     */
    private Date calibrateFinishTime;
    /**
     * 重量校准状态:0-未校准,1-合格,2-不合格
     */
    private Integer weightCalibrateStatus;
    /**
     * 体积校准状态:0-未校准,1-合格,2-不合格
     */
    private Integer volumeCalibrateStatus;
    /**
     * 设备状态:0-未校准,1-合格,2-不合格
     */
    private Integer machineStatus;

    /**
     * 校准任务开始时间
     */
    private Date calibrateTaskStartTime;
    /**
     * 校准任务结束时间
     */
    private Date calibrateTaskEndTime;

    /**
     * 任务创建人erp
     */
    private String createUserErp;

    /**
     * 分页查询offset
     */
    private int offset;

    /**
     * 分页查询limit
     */
    private int limit;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Date getCalibrateTime() {
        return calibrateTime;
    }

    public void setCalibrateTime(Date calibrateTime) {
        this.calibrateTime = calibrateTime;
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

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getWeightCalibrateTime() {
        return weightCalibrateTime;
    }

    public void setWeightCalibrateTime(Date weightCalibrateTime) {
        this.weightCalibrateTime = weightCalibrateTime;
    }

    public Date getVolumeCalibrateTime() {
        return volumeCalibrateTime;
    }

    public void setVolumeCalibrateTime(Date volumeCalibrateTime) {
        this.volumeCalibrateTime = volumeCalibrateTime;
    }

    public Date getCalibrateFinishTime() {
        return calibrateFinishTime;
    }

    public void setCalibrateFinishTime(Date calibrateFinishTime) {
        this.calibrateFinishTime = calibrateFinishTime;
    }

    public Integer getWeightCalibrateStatus() {
        return weightCalibrateStatus;
    }

    public void setWeightCalibrateStatus(Integer weightCalibrateStatus) {
        this.weightCalibrateStatus = weightCalibrateStatus;
    }

    public Integer getVolumeCalibrateStatus() {
        return volumeCalibrateStatus;
    }

    public void setVolumeCalibrateStatus(Integer volumeCalibrateStatus) {
        this.volumeCalibrateStatus = volumeCalibrateStatus;
    }

    public Integer getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(Integer machineStatus) {
        this.machineStatus = machineStatus;
    }

    public Date getCalibrateTaskStartTime() {
        return calibrateTaskStartTime;
    }

    public void setCalibrateTaskStartTime(Date calibrateTaskStartTime) {
        this.calibrateTaskStartTime = calibrateTaskStartTime;
    }

    public Date getCalibrateTaskEndTime() {
        return calibrateTaskEndTime;
    }

    public void setCalibrateTaskEndTime(Date calibrateTaskEndTime) {
        this.calibrateTaskEndTime = calibrateTaskEndTime;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
