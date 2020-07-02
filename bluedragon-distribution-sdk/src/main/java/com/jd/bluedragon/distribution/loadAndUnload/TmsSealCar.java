package com.jd.bluedragon.distribution.loadAndUnload;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @Description 运输封车信息
 * @date 2020/6/25 21:48
 */
public class TmsSealCar implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封车编码
     * */
    private String sealCarCode;

    /**
     * 操作状态：10-封车，20-解封，30-出围栏，40-进围栏
     * */
    private Integer stauts;

    /**
     * 操作人ERP
     * */
    private String operateUserCode;

    /**
     * 操作人姓名
     * */
    private String operateUserName;

    /**
     * 操作时间
     * */
    private Date operateTime;

    /**
     * 封车方式：10-按运力，20-按派车任务明细，30按运力（新）
     * */
    private Integer sealCarType;

    /**
     * 批次号集合
     * */
    private List<String> batchCodes;

    /**
     * 委托书编码
     * */
    private String transBookCode;

    /**
     * 体积
     * */
    private Double volume;

    /**
     * 重量
     * */
    private Double weight;

    /**
     * 运输方式：1-公路零担，2-公路整车，3-航空，4-铁路，5-快递，
     * 6-冷链整车，7-冷链零担，8-公路整车平板，9-公路零担平板，10-铁路零担，11-海运
     * */
    private Integer transWay;

    /**
     * 车牌号
     * */
    private String vehicleNumber;

    /**
     * 操作站点ID
     * */
    private Integer operateSiteId;

    /**
     * 操作站点编码
     * */
    private String operateSiteCode;

    /**
     * 操作站点名称
     * */
    private String operateSiteName;

    /**
     * 接货仓编码
     * */
    private String warehouseCode;

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getStauts() {
        return stauts;
    }

    public void setStauts(Integer stauts) {
        this.stauts = stauts;
    }

    public String getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(String operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public String getTransBookCode() {
        return transBookCode;
    }

    public void setTransBookCode(String transBookCode) {
        this.transBookCode = transBookCode;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}
