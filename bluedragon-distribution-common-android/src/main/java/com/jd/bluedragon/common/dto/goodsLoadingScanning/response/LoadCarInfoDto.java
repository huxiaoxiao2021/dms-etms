package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 根据车牌查询容积配置
 * @author: wuming
 * @create: 2021-02-23 17:21
 */
public class LoadCarInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准容积,立方米
     */
    private Double volume;

    /**
     * 核定载重,吨
     */
    private Double weight;

    /**
     * 车型名称
     */
    private String vehicleTypeName;

    public LoadCarInfoDto() {
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public LoadCarInfoDto(Double volume, Double weight, String vehicleTypeName) {
        this.volume = volume;
        this.weight = weight;
        this.vehicleTypeName = vehicleTypeName;
    }


}
