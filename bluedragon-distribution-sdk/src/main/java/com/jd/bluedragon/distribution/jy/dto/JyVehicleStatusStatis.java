package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;

/**
 * @ClassName VehicleStatus
 * @Description
 * @Author wyh
 * @Date 2022/3/2 16:53
 **/
public class JyVehicleStatusStatis implements Serializable {

    private static final long serialVersionUID = 168767290763647636L;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 车辆状态描述
     */
    private String vehicleStatusName;

    /**
     * 总数
     */
    private Long total;

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleStatusName() {
        return vehicleStatusName;
    }

    public void setVehicleStatusName(String vehicleStatusName) {
        this.vehicleStatusName = vehicleStatusName;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
