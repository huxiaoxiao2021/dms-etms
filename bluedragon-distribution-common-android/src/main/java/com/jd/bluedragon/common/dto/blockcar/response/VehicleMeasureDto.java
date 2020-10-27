package com.jd.bluedragon.common.dto.blockcar.response;

import java.io.Serializable;

public class VehicleMeasureDto implements Serializable {

    /** 车牌号 */
    private String vehicleNumber;

    /** 重量 */
    private Double weight;

    /** 体积 */
    private Double volume;

    @Override
    public String toString() {
        return getVehicleNumber()==null ? "" : getVehicleNumber();
    }

    public VehicleMeasureDto() {}

    public VehicleMeasureDto(String vehicleNumber, Double volume, Double weight) {
        this.vehicleNumber = vehicleNumber;
        this.volume = volume;
        this.weight = weight;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }
}
