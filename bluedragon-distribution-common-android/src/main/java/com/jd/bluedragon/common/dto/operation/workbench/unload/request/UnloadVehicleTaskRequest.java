package com.jd.bluedragon.common.dto.operation.workbench.unload.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName UnloadVehicleTaskRequest
 * @Description 卸车任务请求
 * @Author wyh
 * @Date 2022/4/1 17:16
 **/
public class UnloadVehicleTaskRequest implements Serializable {

    private static final long serialVersionUID = -4901280308875214602L;

    private Integer pageNumber;

    private Integer pageSize;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 目的分拣中心
     */
    private Integer endSiteCode;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 包裹号|车牌号后四位
     */
    private String barCode;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * search|refresh
     */
    private String fetchType;

    private Integer taskType;

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

    public Integer getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(Integer endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }
}
