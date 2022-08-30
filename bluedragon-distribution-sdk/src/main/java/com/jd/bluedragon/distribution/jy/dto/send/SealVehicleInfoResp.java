package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class SealVehicleInfoResp implements Serializable {
    private static final long serialVersionUID = 4862292928579823809L;

    /**
     * 运力编码
     * */
    private String transportCode;
    /**
     * 托盘数
     */
    private String tray;
    /**
     * 已扫总量总计/千克
     */
    private BigDecimal weight;
    /**
     * 已扫体积总计/立方厘米
     */
    private BigDecimal volume;
    /**
     * 车牌号
     */
    private String vehicleNumber;

    private List<String> sendCodeList;

    public List<String> getSendCodeList() {
        return sendCodeList;
    }

    public void setSendCodeList(List<String> sendCodeList) {
        this.sendCodeList = sendCodeList;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTray() {
        return tray;
    }

    public void setTray(String tray) {
        this.tray = tray;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

}
