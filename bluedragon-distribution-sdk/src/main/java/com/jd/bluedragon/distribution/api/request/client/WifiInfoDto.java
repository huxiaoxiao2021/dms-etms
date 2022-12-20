package com.jd.bluedragon.distribution.api.request.client;

import java.io.Serializable;

/**
 * wifi信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-06 21:06:09 周二
 */
public class WifiInfoDto implements Serializable {

    private static final long serialVersionUID = -7978760383376931839L;
    /**
     * wifi名称
     */
    private String ssid;

    /**
     * wifi mac地址
     */
    private String bssid;

    /**
     * wifi信号强度
     */
    private Integer level;

    /**
     * 手机距离wifi的距离
     */
    private Double distance;

    /**
     * wifi信号频率
     */
    private Integer frequency;

    public WifiInfoDto(String ssid, String bssid, Integer level, Double distance, Integer frequency) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.level = level;
        this.distance = distance;
        this.frequency = frequency;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
