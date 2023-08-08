package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/8 15:33
 * @Description
 */
public class AviationToSealAndSealedListRes implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    /**
     * 任务状态
     */
    private Integer taskStatus;
    private Integer taskStatusName;
    /**
     * 车辆状态数量统计
     */
    private List<TaskStatusStatistics> taskStatusStatisticsList;

    private List<AviationSendTaskDto> aviationSendTaskDtoList;

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getTaskStatusName() {
        return taskStatusName;
    }

    public void setTaskStatusName(Integer taskStatusName) {
        this.taskStatusName = taskStatusName;
    }

    public List<TaskStatusStatistics> getTaskStatusStatisticsList() {
        return taskStatusStatisticsList;
    }

    public void setTaskStatusStatisticsList(List<TaskStatusStatistics> taskStatusStatisticsList) {
        this.taskStatusStatisticsList = taskStatusStatisticsList;
    }

    public List<AviationSendTaskDto> getAviationSendTaskDtoList() {
        return aviationSendTaskDtoList;
    }

    public void setAviationSendTaskDtoList(List<AviationSendTaskDto> aviationSendTaskDtoList) {
        this.aviationSendTaskDtoList = aviationSendTaskDtoList;
    }
}
