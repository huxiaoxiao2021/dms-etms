package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/8 15:12
 * @Description
 */
public class TaskStatusStatisticsRes  implements Serializable {

    private static final long serialVersionUID = 168767290763647636L;

    private List<TaskStatusStatistics> taskStatusStatisticsList;


    public List<TaskStatusStatistics> getTaskStatusStatisticsList() {
        return taskStatusStatisticsList;
    }

    public void setTaskStatusStatisticsList(List<TaskStatusStatistics> taskStatusStatisticsList) {
        this.taskStatusStatisticsList = taskStatusStatisticsList;
    }
}
