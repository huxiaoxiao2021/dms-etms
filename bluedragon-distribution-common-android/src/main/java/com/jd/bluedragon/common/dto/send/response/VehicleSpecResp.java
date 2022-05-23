package com.jd.bluedragon.common.dto.send.response;


import java.io.Serializable;
import java.util.List;

public class VehicleSpecResp implements Serializable {
    private static final long serialVersionUID = -7846057809543529300L;
    /**
     * 车身长
     */
    private String vehicleLength;
    /**
     * 某规格下的车辆类型列表信息
     */
    private List<VehicleTypeDto> vehicleTypeDtoList;

    public String getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(String vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public List<VehicleTypeDto> getVehicleTypeDtoList() {
        return vehicleTypeDtoList;
    }

    public void setVehicleTypeDtoList(List<VehicleTypeDto> vehicleTypeDtoList) {
        this.vehicleTypeDtoList = vehicleTypeDtoList;
    }
}
