package com.jd.bluedragon.distribution.api.request.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 客户端位置信息 param object
 * @author fanggang7
 * @time 2022-11-10 15:50:29 周四
 */
public class DeviceLocationInfo implements Serializable {

    private static final long serialVersionUID = -54592365644078267L;

    /**
     * ipv4
     */
    private String ipv4;

    /**
     * ipv6
     */
    private String ipv6;

    /**
     * 本设备mac地址
     */
    private String macAddressSelf;

    /**
     * 连接网络的mac地址
     */
    private String macAddressNetwork;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 设备附近WIFI信息
     */
    private List<WifiInfoDto> wifiInfoList;

    private BigDecimal distanceToSite;

    /**
     * WiFi名称
     */
    private String wifiSsid;

    /**
     * WiFi的BSSID
     */
    private String wifiBssid;

    /**
     * 取信号强度
     */
    private Integer wifiRssi;

    /**
     * 网络ID
     */
    private Integer networkId;

    /**
     * 网络连接速度
     */
    private Integer networkLinkSpeed;

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public String getMacAddressSelf() {
        return macAddressSelf;
    }

    public void setMacAddressSelf(String macAddressSelf) {
        this.macAddressSelf = macAddressSelf;
    }

    public String getMacAddressNetwork() {
        return macAddressNetwork;
    }

    public void setMacAddressNetwork(String macAddressNetwork) {
        this.macAddressNetwork = macAddressNetwork;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public List<WifiInfoDto> getWifiInfoList() {
        return wifiInfoList;
    }

    public void setWifiInfoList(List<WifiInfoDto> wifiInfoList) {
        this.wifiInfoList = wifiInfoList;
    }

    public BigDecimal getDistanceToSite() {
        return distanceToSite;
    }

    public void setDistanceToSite(BigDecimal distanceToSite) {
        this.distanceToSite = distanceToSite;
    }

    public String getWifiSsid() {
        return wifiSsid;
    }

    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    public String getWifiBssid() {
        return wifiBssid;
    }

    public void setWifiBssid(String wifiBssid) {
        this.wifiBssid = wifiBssid;
    }

    public Integer getWifiRssi() {
        return wifiRssi;
    }

    public void setWifiRssi(Integer wifiRssi) {
        this.wifiRssi = wifiRssi;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Integer networkId) {
        this.networkId = networkId;
    }

    public Integer getNetworkLinkSpeed() {
        return networkLinkSpeed;
    }

    public void setNetworkLinkSpeed(Integer networkLinkSpeed) {
        this.networkLinkSpeed = networkLinkSpeed;
    }
}
