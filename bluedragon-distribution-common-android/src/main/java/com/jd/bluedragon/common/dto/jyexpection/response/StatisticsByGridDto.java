package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 按网格统计
 */
public class StatisticsByGridDto implements Serializable {

    // 楼层
    private Integer floor;

    // 网格号
    private String gridNo;

    // 网格编码
    private String gridCode;

    // 作业区编号
    private String areaCode;

    // 作业区
    private String areaName;

    // 待取数量
    private Integer pendingNum;

    // 超时数量
    private Integer timeoutNum;

    private List<TagDto> tags;

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getPendingNum() {
        return pendingNum;
    }

    public void setPendingNum(Integer pendingNum) {
        this.pendingNum = pendingNum;
    }

    public Integer getTimeoutNum() {
        return timeoutNum;
    }

    public void setTimeoutNum(Integer timeoutNum) {
        this.timeoutNum = timeoutNum;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }
}
