package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/25 21:40
 * @Description: 待领取异常任务
 */
public class ExpTaskOfWaitReceiveDto implements Serializable {

    //网格编号
    private String gridCode;

    //网格号
    private String gridNo;

    //工作区名称
    private String areaName;

    //待取任务数
    private Integer count;

    //
    private List<ExpTaskDto> taskDtos;

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<ExpTaskDto> getTaskDtos() {
        return taskDtos;
    }

    public void setTaskDtos(List<ExpTaskDto> taskDtos) {
        this.taskDtos = taskDtos;
    }
}
