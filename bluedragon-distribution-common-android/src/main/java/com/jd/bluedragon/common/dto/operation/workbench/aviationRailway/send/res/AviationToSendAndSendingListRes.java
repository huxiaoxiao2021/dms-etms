package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:59
 * @Description
 */
public class AviationToSendAndSendingListRes implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    /**
     * 任务状态
     */
    private Integer taskStatus;
    private String taskStatusName;

    /**
     * 车辆状态数量统计
     */
    private List<TaskStatusStatistics> taskStatusStatisticsList;
    /**
     * 流向
     */
    private List<AviationSendTaskNextSiteList> nextSiteListList;


    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskStatusName() {
        return taskStatusName;
    }

    public void setTaskStatusName(String taskStatusName) {
        this.taskStatusName = taskStatusName;
    }

    public List<TaskStatusStatistics> getTaskStatusStatisticsList() {
        return taskStatusStatisticsList;
    }

    public void setTaskStatusStatisticsList(List<TaskStatusStatistics> taskStatusStatisticsList) {
        this.taskStatusStatisticsList = taskStatusStatisticsList;
    }

    public List<AviationSendTaskNextSiteList> getNextSiteListList() {
        return nextSiteListList;
    }

    public void setNextSiteListList(List<AviationSendTaskNextSiteList> nextSiteListList) {
        this.nextSiteListList = nextSiteListList;
    }
}
