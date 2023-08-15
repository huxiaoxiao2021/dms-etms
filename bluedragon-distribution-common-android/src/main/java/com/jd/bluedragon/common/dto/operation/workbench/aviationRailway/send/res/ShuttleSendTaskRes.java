package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/15 14:46
 * @Description
 */
public class ShuttleSendTaskRes implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 车辆状态数量统计
     */
    private List<TaskStatusStatistics> taskStatusStatisticsList;

    private List<ShuttleSendTaskDto> shuttleSendTaskDtoList;

    public List<TaskStatusStatistics> getTaskStatusStatisticsList() {
        return taskStatusStatisticsList;
    }

    public void setTaskStatusStatisticsList(List<TaskStatusStatistics> taskStatusStatisticsList) {
        this.taskStatusStatisticsList = taskStatusStatisticsList;
    }

    public List<ShuttleSendTaskDto> getShuttleSendTaskDtoList() {
        return shuttleSendTaskDtoList;
    }

    public void setShuttleSendTaskDtoList(List<ShuttleSendTaskDto> shuttleSendTaskDtoList) {
        this.shuttleSendTaskDtoList = shuttleSendTaskDtoList;
    }
}
