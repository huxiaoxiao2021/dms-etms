package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 任务列表
 */
public class ExpTaskDto implements Serializable {
    //任务id
    private String taskId;

    //提交条码
    private String barCode;

    //停留时间 hh:mm
    private String stayTime;

    //楼层
    private String floor;

    //网格编号
    private String gridCode;

    //网格号
    private String gridNo;

    //工作区名称
    private String areaName;

    //提报人姓名
    private String reporterName;

    //标签列表
    private String tags;

    //是否保存过
    private String saved;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getStayTime() {
        return stayTime;
    }

    public void setStayTime(String stayTime) {
        this.stayTime = stayTime;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

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

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }
}
