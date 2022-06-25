package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SendVehicleTaskRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/17 20:53
 **/
public class SendVehicleTaskRequest implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    private Integer pageNumber;

    private Integer pageSize;

    private User user;

    private CurrentOperate currentOperate;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
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
