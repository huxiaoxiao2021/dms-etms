package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.List;

public class AirRailDto implements Serializable {
    private static final long serialVersionUID = -5631376523517894268L;

    /**
     * 始发机场/车站编码
     */
    private String beginNodeCode;
    /**
     * 始发机场/车站名称
     */
    private String beginNodeName;
    /**
     * 待提航班/车次任务数
     */
    private Integer waitScanTaskNum;
    /**
     * 是否无任务分组
     */
    private Boolean noTaskFlag;

    /**
     * 待提总件数
     */
    private Integer waitScanTotal;

    /**
     * 已提总件数
     */
    private Integer haveScannedTotal;

    /**
     * 多提总件数
     */
    private Integer multipleScanTotal;

    /**
     * 机场下有多少个任务
     */
    private Integer taskNum;

    /**
     * 机场下的自任务列表
     */
    private List<AirRailTaskAggDto> taskAggDtoList;

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public Integer getWaitScanTaskNum() {
        return waitScanTaskNum;
    }

    public void setWaitScanTaskNum(Integer waitScanTaskNum) {
        this.waitScanTaskNum = waitScanTaskNum;
    }

    public Boolean getNoTaskFlag() {
        return noTaskFlag;
    }

    public void setNoTaskFlag(Boolean noTaskFlag) {
        this.noTaskFlag = noTaskFlag;
    }

    public Integer getWaitScanTotal() {
        return waitScanTotal;
    }

    public void setWaitScanTotal(Integer waitScanTotal) {
        this.waitScanTotal = waitScanTotal;
    }

    public Integer getHaveScannedTotal() {
        return haveScannedTotal;
    }

    public void setHaveScannedTotal(Integer haveScannedTotal) {
        this.haveScannedTotal = haveScannedTotal;
    }

    public Integer getMultipleScanTotal() {
        return multipleScanTotal;
    }

    public void setMultipleScanTotal(Integer multipleScanTotal) {
        this.multipleScanTotal = multipleScanTotal;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public List<AirRailTaskAggDto> getTaskAggDtoList() {
        return taskAggDtoList;
    }

    public void setTaskAggDtoList(List<AirRailTaskAggDto> taskAggDtoList) {
        this.taskAggDtoList = taskAggDtoList;
    }
}
