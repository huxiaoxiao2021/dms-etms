package com.jd.bluedragon.distribution.jy.dto.seal;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @Author zhengchengfa
 * @Date 2023/11/15 18:40
 * @Description
 */
public class JyAviationSealMq implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;


    /**
     * 操作场地
     */
    private Integer startSiteId;
    private String startSiteCode;
    /**
     * 流向场地
     */
    private Integer nextSiteId;
    private String nextSiteName;
    /**
     * 操作人
     */
    private String operatorErp;
    /**
     * 操作时间
     */
    private Long operateTime;
    /**
     * 航班号
     */
    private String flightNumber;
    /**
     * 批次信息
     */
    private List<String> batchCodeList;
    /**
     * 件数
     */
    private Integer itemNum;
    private Double weight;
    /**
     * 操作场地
     */
    private Integer airType;
    private Integer taskType;
    private String beginNodeCode;
    private String beginNodeName;
    private String endNodeCode;
    private String endNodeName;
    private String bookingCode;


    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public List<String> getBatchCodeList() {
        return batchCodeList;
    }

    public void setBatchCodeList(List<String> batchCodeList) {
        this.batchCodeList = batchCodeList;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getAirType() {
        return airType;
    }

    public void setAirType(Integer airType) {
        this.airType = airType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

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

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }
}
