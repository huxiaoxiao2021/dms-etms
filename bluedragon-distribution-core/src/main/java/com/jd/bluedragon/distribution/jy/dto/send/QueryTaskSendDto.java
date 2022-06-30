package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName QueryTaskSendDto
 * @Description
 * @Author wyh
 * @Date 2022/6/9 22:00
 **/
public class QueryTaskSendDto implements Serializable {

    private static final long serialVersionUID = 194960511461299312L;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * 车辆状态
     */
    private List<Integer> vehicleStatuses;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 始发场地
     */
    private Long startSiteId;

    /**
     * 目的地场地
     */
    private Long endSiteId;

    /**
     * 搜索关键字
     */
    private String keyword;

    private List<String> sendVehicleBizList;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Integer> getVehicleStatuses() {
        return vehicleStatuses;
    }

    public void setVehicleStatuses(List<Integer> vehicleStatuses) {
        this.vehicleStatuses = vehicleStatuses;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public List<String> getSendVehicleBizList() {
        return sendVehicleBizList;
    }

    public void setSendVehicleBizList(List<String> sendVehicleBizList) {
        this.sendVehicleBizList = sendVehicleBizList;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
