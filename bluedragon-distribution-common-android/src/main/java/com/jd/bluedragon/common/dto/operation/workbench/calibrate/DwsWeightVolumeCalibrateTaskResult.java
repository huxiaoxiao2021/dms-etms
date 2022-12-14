package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备称重量方校准返回体
 *
 * @author hujiping
 * @date 2022/12/7 8:13 PM
 */
public class DwsWeightVolumeCalibrateTaskResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 待处理任务
     */
    private List<DwsWeightVolumeCalibrateTaskDetail> todoTaskList
            = new ArrayList<DwsWeightVolumeCalibrateTaskDetail>();

    /**
     * 已完成任务
     */
    private List<DwsWeightVolumeCalibrateTaskDetail> doneTaskList
            = new ArrayList<DwsWeightVolumeCalibrateTaskDetail>();

    /**
     * 超时任务
     */
    private List<DwsWeightVolumeCalibrateTaskDetail> overtimeTaskList
            = new ArrayList<DwsWeightVolumeCalibrateTaskDetail>();

    /**
     * 服务器系统时间
     */
    private Long systemTime;

    public List<DwsWeightVolumeCalibrateTaskDetail> getTodoTaskList() {
        return todoTaskList;
    }

    public void setTodoTaskList(List<DwsWeightVolumeCalibrateTaskDetail> todoTaskList) {
        this.todoTaskList = todoTaskList;
    }

    public List<DwsWeightVolumeCalibrateTaskDetail> getDoneTaskList() {
        return doneTaskList;
    }

    public void setDoneTaskList(List<DwsWeightVolumeCalibrateTaskDetail> doneTaskList) {
        this.doneTaskList = doneTaskList;
    }

    public List<DwsWeightVolumeCalibrateTaskDetail> getOvertimeTaskList() {
        return overtimeTaskList;
    }

    public void setOvertimeTaskList(List<DwsWeightVolumeCalibrateTaskDetail> overtimeTaskList) {
        this.overtimeTaskList = overtimeTaskList;
    }

    public Long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(Long systemTime) {
        this.systemTime = systemTime;
    }
}
