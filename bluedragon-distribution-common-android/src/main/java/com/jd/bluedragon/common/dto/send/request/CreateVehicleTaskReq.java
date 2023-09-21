package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CreateVehicleTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -5531009617431175788L;

    /**
     * 车型
     */
    private Integer vehicleType;
    /**
     * 车型名称
     */
    private String vehicleTypeName;
    /**
     * 目的地ID
     */
    private Long destinationSiteId;

    /**
     * 确定创建自建任务
     */
    private Boolean confirmCreate;

    public Integer getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public Long getDestinationSiteId() {
        return destinationSiteId;
    }

    public void setDestinationSiteId(Long destinationSiteId) {
        this.destinationSiteId = destinationSiteId;
    }

    public Boolean getConfirmCreate() {
        return confirmCreate;
    }

    public void setConfirmCreate(Boolean confirmCreate) {
        this.confirmCreate = confirmCreate;
    }
}
