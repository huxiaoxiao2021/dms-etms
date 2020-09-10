package com.jd.bluedragon.common.dto.search.response;

import java.io.Serializable;

public class PackWeightVOResponse implements Serializable {
    private static final long serialVersionUID = 9015640463647589704L;

    /**
     * 重量 单位kg*/
    private Double weight;

    /**
     * 体积 单位立方厘米*/
    private Double volume;

    /**
     * 长 单位厘米
     */
    private Double length;

    /**
     * 宽 单位厘米
     */
    private Double width;

    /**
     * 高 单位厘米
     */
    private Double high;

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

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }
}
