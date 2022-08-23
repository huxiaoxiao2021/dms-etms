package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 任务列表
 */
public class ExpTaskDto implements Serializable {
    //业务ID
    private String bizId;

    // 提报入口
    private Integer source;

    //提交条码
    private String barCode;

    //停留时间 hh:mm
    private String stayTime;

    //楼层
    private Integer floor;

    //网格编号
    private String gridCode;

    //网格号
    private String gridNo;

    //工作区名称
    private String areaName;

    //提报人姓名
    private String reporterName;

    //标签列表
    private List<TagDto> tags;

    //是否保存过
    private boolean saved;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
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

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public boolean getSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
