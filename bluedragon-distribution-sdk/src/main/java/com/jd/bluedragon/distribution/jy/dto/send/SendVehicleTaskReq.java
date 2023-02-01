package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

/**
 * @ClassName SendVehicleTaskRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/17 20:53
 **/
public class SendVehicleTaskReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 目的地场地
     */
    private Long endSiteId;

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

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }
}
