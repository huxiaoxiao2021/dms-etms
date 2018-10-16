package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class WeightMeasureRequest extends JdRequest {
    /**
     * 扫描到的包裹号/箱号
     */
    private String barCode;

    /**
     * 重量 单位kg
     */
    private Double weight;

    /**
     * 体积 单位 cm³
     */
    private Double volume;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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
