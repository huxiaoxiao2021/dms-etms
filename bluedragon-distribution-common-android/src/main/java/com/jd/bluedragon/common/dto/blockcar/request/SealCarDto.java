package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;
import java.util.List;

/**
 * SealCarDto
 * 封车dto
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class SealCarDto implements Serializable {
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

    /**
     * 托盘数
     */
    private String palletCount;

    /**
     * 运力类型
     */
    private Integer transWay;

    /**
     * 运力类型名称
     */
    private String transWayName;
    
    /**
     * 预封车选择的批次号列表
     */
    private List<String> selectedSendCodes;

    private String billCode;

    @Override
    public String toString() {
        return "SealCarDto{" +
                "sealCarType=" + sealCarType +
                ", itemSimpleCode='" + itemSimpleCode + '\'' +
                ", transportCode='" + transportCode + '\'' +
                ", batchCodes=" + batchCodes +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", sealCodes=" + sealCodes +
                ", weight=" + weight +
                ", volume=" + volume +
                ", routeLineCode='" + routeLineCode + '\'' +
                ", sealCarTime='" + sealCarTime + '\'' +
                ", sealSiteId=" + sealSiteId +
                ", sealSiteName='" + sealSiteName + '\'' +
                ", sealUserCode='" + sealUserCode + '\'' +
                ", sealUserName='" + sealUserName + '\'' +
                ", palletCount='" + palletCount + '\'' +
                ", transWay='" + transWay + '\'' +
                ", transWayName='" + transWayName + '\'' +
                ", billCode='" + billCode + '\'' +
                '}';
    }

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

    public String getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(String palletCount) {
        this.palletCount = palletCount;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public String getTransWayName() {
        return transWayName;
    }

    public void setTransWayName(String transWayName) {
        this.transWayName = transWayName;
    }

	public List<String> getSelectedSendCodes() {
		return selectedSendCodes;
	}

	public void setSelectedSendCodes(List<String> selectedSendCodes) {
		this.selectedSendCodes = selectedSendCodes;
	}

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }
}
