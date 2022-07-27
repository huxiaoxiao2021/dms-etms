package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


public class JySendArriveStatusDto implements Serializable{
    /**
     * 操作场地
     */
    private Long operateSiteId;
    /**
     * 操作人erp
     */
    private String operateUserErp;
    /**
     * 操作人姓名
     */
    private String operateUserName;
    /**
     * 派车单号
     */
    private String transWorkCode;
    /**
     * 车辆是否到达 0-未到 1-已到
     */
    private Integer vehicleArrived;
    /**
     * 拍照时间
     */
    private Long operateTime;
    /**
     * 照片URL链接
     */
    private List<String> imgList;

    public Long getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Long operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

    public Integer getVehicleArrived() {
        return vehicleArrived;
    }

    public void setVehicleArrived(Integer vehicleArrived) {
        this.vehicleArrived = vehicleArrived;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
