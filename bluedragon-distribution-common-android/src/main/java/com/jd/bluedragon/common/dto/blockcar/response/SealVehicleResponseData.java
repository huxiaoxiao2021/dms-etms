package com.jd.bluedragon.common.dto.blockcar.response;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/18 11:16
 * @Description:
 */
public class SealVehicleResponseData implements Serializable {

    private Integer code;

    private String message;

    private Double weight;
    private Double volume;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
