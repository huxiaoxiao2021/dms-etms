package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备称重校准请求体
 *
 * @author hujiping
 * @date 2022/12/7 8:05 PM
 */
public class DwsWeightVolumeCalibrateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNumber;

    private Integer pageSize;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 设备编码
     */
    private String machineCode;

    /**
     * 校准任务开始时间
     */
    private Date calibrateTaskStartTime;
    /**
     * 校准任务结束时间
     */
    private Date calibrateTaskEndTime;
    /**
     * 强制创建任务
     */
    private Boolean forceCreateTask;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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

    public Boolean getForceCreateTask() {
        return forceCreateTask;
    }

    public void setForceCreateTask(Boolean forceCreateTask) {
        forceCreateTask = forceCreateTask;
    }
}
