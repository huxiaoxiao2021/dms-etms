package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2020/1/9 13:56
 */
public class SealVehicleVolumeVerifyRequest extends JdRequest {

    private static final long serialVersionUID = -4900034488418807075L;

    /**
     * 数据来源
     * */
    private Integer source;

    /**
     * 车牌号
     * */
    private String vehicleNumber;

    /**
     * 运力编码
     * */
    private String transportCode;

    /**
     * 批次号集合
     * */
    private List<String> batchCodes;

    /**
     * 封签号集合
     * */
    private List<String> sealCodes;

    /**
     * 封车时间
     * */
    private String sealCarTime;

    /**
     * 封车站点ID
     * */
    private Integer sealSiteId;

    /**
     * 封车站点7位编码
     * */
    private String sealSiteCode;

    /**
     * 封车站点名称
     * */
    private String sealSiteName;

    /**
     * 封车人ERP
     * */
    private String sealUserCode;

    /**
     * 封车人姓名
     * */
    private String sealUserName;

    /**
     * 封车类型
     * */
    private Integer sealCarType;

    /**
     * 派车任务明细简码
     * */
    private String itemSimpleCode;

    /**
     * 重量
     * */
    private Double weight;

    /**
     * 体积
     * */
    private Double volume;

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
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

    public String getSealSiteCode() {
        return sealSiteCode;
    }

    public void setSealSiteCode(String sealSiteCode) {
        this.sealSiteCode = sealSiteCode;
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
