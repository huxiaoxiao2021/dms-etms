package com.jd.bluedragon.distribution.weight.domain;

import java.io.Serializable;

/**
 * 称重量方实体
 *
 * @author: hujiping
 * @date: 2020/1/3 16:57
 */
public class WeightAndVolumeFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作单号：箱号、运单号、包裹号
     * */
    private String strCode;

    /**
     * 重量KG
     * */
    private Double weight;

    /**
     * 体积CM³
     * */
    private Double volume;

    /**
     * 长CM
     * */
    private Double length;

    /**
     * 宽CM
     * */
    private Double width;

    /**
     * 高CM
     * */
    private Double high;

    /**
     * 操作时间
     * */
    private long opeTime;

    public String getStrCode() {
        return strCode;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
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

    public long getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(long opeTime) {
        this.opeTime = opeTime;
    }
}
