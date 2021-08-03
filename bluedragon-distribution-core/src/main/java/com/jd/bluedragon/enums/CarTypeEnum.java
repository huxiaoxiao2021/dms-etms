package com.jd.bluedragon.enums;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.LoadCarInfoDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 车辆容积校验固定车型信息
 * @author: wuming
 * @create: 2021-05-26 14:04
 */
public enum CarTypeEnum {

    TRUCK_420CM("4.2米货车", 12.0D, 2.5D),
    TRUCK_580CM("5.8米货车", 25.0D, 5.0D),
    TRUCK_680CM("6.8米货车", 35.0D, 10.0D),
    TRUCK_760CM("7.6米货车", 40.0D, 8.0D),
    TRUCK_860CM("8.6米货车", 50.0D, 10.0D),
    TRUCK_960CM("9.6米货车", 70.0D, 26.0D),
    TRUCK_1250CM("12.5米货车", 78.0D, 22.0D),
    TRUCK_1450CM("14.5米货车", 94.0D, 26.0D),
    TRUCK_1650CM("16.5米货车", 110.0D, 24.0D),
    TRUCK_1750CM("17.5米货车", 123.0D, 26.0D),
    TRUCK_CHENG_PEI("城配装车", 9999.0D, 9999D);

    private String carTypeName;
    private Double volume;
    private Double weight;

    CarTypeEnum(String carTypeName, Double volume, Double weight) {
        this.carTypeName = carTypeName;
        this.volume = volume;
        this.weight = weight;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
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

    /**
     * 获取固定车型列表
     *
     * @return
     */
    public static List<LoadCarInfoDto> getCarTypeList() {
        List<LoadCarInfoDto> carInfoDtoList = new ArrayList<>();
        for (CarTypeEnum typeEnum : CarTypeEnum.values()) {
            LoadCarInfoDto dto = new LoadCarInfoDto();
            dto.setVehicleTypeName(typeEnum.getCarTypeName());
            dto.setVolume(typeEnum.getVolume());
            dto.setWeight(typeEnum.weight);
            carInfoDtoList.add(dto);
        }
        return carInfoDtoList;
    }

}
