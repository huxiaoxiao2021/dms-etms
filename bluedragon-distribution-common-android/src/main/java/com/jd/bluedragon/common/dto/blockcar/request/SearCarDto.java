package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;
import java.util.List;

/**
 * SearCarDto
 * 封车dto
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class SearCarDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 封车类型
     */
    private Integer sealCarType;

    /**
     * 任务简码
     */
    private String itemSimpleCode;

    /**
     * 运力编码
     */
    private String transportCode;

    /**
     * 批次信息
     */
    private List<String> batchCodes;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 封车号
     */
    private List<String> sealCodes;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 体积
     */
    private Double volume;

    /**
     * 线路
     */
    private String routeLineCode;

    /**
     * 封车时间
     */
    private String sealCarTime;

    /**
     * 分拣中心id
     */
    private Integer sealSiteId;

    /**
     * 分拣中心名称
     */
    private String sealSiteName;

    /**
     * 用户erp账号
     */
    private String sealUserCode;

    /**
     * 用户姓名
     */
    private String sealUserName;

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public String getItemSimpleCode() {
        return itemSimpleCode;
    }

    public void setItemSimpleCode(String itemSimpleCode) {
        this.itemSimpleCode = itemSimpleCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
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

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(String sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public Integer getSealSiteId() {
        return sealSiteId;
    }

    public void setSealSiteId(Integer sealSiteId) {
        this.sealSiteId = sealSiteId;
    }

    public String getSealSiteName() {
        return sealSiteName;
    }

    public void setSealSiteName(String sealSiteName) {
        this.sealSiteName = sealSiteName;
    }

    public String getSealUserCode() {
        return sealUserCode;
    }

    public void setSealUserCode(String sealUserCode) {
        this.sealUserCode = sealUserCode;
    }

    public String getSealUserName() {
        return sealUserName;
    }

    public void setSealUserName(String sealUserName) {
        this.sealUserName = sealUserName;
    }
}
