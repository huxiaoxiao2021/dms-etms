package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * 站点包裹尺寸
 * <p>
 * <p>
 * Created by lixin39 on 2018/1/30.
 */
public class ThirdPartyOverrunRequest implements Serializable {

    /**
     * 站点编号
     */
    private Integer siteCode;

    /**
     * 包裹
     */
    private String packageCode;

    /**
     * 重量, 单位 立方厘米
     */
    private Double weight;

    /**
     * 体积, 单位 kg
     */
    private Double volume;

    /**
     * 长, 单位 厘米
     */
    private Double length;

    /**
     * 宽, 单位 厘米
     */
    private Double width;

    /**
     * 高, 单位 厘米
     */
    private Double height;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

}
