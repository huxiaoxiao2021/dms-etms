package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.util.List;
//todo laoqingchang
public class SendFlowRes {
    /**
     * 展示类型
     */
    private Integer displayType;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 最大添加流向数量
     */
    private Integer maxFlowNum;

    /**
     * 流向信息
     */
    private List<SendFlowDto> flowDtoList;

    public Integer getDisplayType() {
        return displayType;
    }

    public void setDisplayType(Integer displayType) {
        this.displayType = displayType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getMaxFlowNum() {
        return maxFlowNum;
    }

    public void setMaxFlowNum(Integer maxFlowNum) {
        this.maxFlowNum = maxFlowNum;
    }

    public List<SendFlowDto> getFlowDtoList() {
        return flowDtoList;
    }

    public void setFlowDtoList(List<SendFlowDto> flowDtoList) {
        this.flowDtoList = flowDtoList;
    }
}
