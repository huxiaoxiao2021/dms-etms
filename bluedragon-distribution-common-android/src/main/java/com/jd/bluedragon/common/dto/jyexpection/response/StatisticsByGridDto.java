package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;

/**
 * 按网格统计
 */
public class StatisticsByGridDto implements Serializable {

    // 网格号
    private String gridNo;

    // 作业区编号
    private String areaCode;

    // 作业区
    private String areaName;

    // 待取数量
    private Integer pendingNum;

    // 超时数量
    private Integer timeoutNum;

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
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
}
