package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 按网格统计
 */
public class ProcessingNumByGridDto implements Serializable {

    // 楼层
    private Integer floor;

    // 网格号
    private String gridNo;

    // 网格编码
    private String griCode;

    // 作业区编号
    private String areaCode;

    // 进行中的数据量
    private Integer processingNum;

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

    public String getGriCode() {
        return griCode;
    }

    public void setGriCode(String griCode) {
        this.griCode = griCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getProcessingNum() {
        return processingNum;
    }

    public void setProcessingNum(Integer processingNum) {
        this.processingNum = processingNum;
    }
}
