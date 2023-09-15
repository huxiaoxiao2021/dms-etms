package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 18:31
 * @Description
 */
public class ShuttleTaskSealCarReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private String bizId;

    private String detailBizId;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 封车类型
     * SealCarTypeEnum
     *  SEAL_BY_TRANSPORT_CAPABILITY(10, "按运力"),
     *  SEAL_BY_TASK(20, "按派车任务明细");
     */
    private Integer sealCarType;
    /**
     * 运力编码
     */
    private String transportCode;
    /**
     * 标准出发时间
     */
    private String departureTimeStr;
    //kg
    private Double weight;
    //立方米
    private Double volume;
    //件数
    private Integer itemNum;
    /**
     * 托盘数
     */
    private Integer palletCount;

    /**
     * 扫描批次号
     */
    private List<String> scanBatchCodes;

    /**
     * 扫描批次号的总重量总体积
     */
    private Double scanBatchCodeTotalWeight;
    private Double scanBatchCodeTotalVolume;
    private Double scanBatchCodeTotalItemNum;
    /**
     * 封签号
     */
    private List<String> scanSealCodes;
    /**
     * TransTypeEnum
     * 运输方式
     *  1、公路零担
     *  2、公路整车
     *  3、航空
     *  4、铁路整车
     *  5、快递
     *  6、冷链整车
     *  7、冷链零担
     *  8、公路整车平板
     *  9、公路零担平板
     *  10、铁路零担
     *
     *  根据任务简码查询任务详情时返回的
     * */
    private Integer transWay;
    /**
     * 运输方式名称
     */
    private String transWayName;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getDetailBizId() {
        return detailBizId;
    }

    public void setDetailBizId(String detailBizId) {
        this.detailBizId = detailBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getDepartureTimeStr() {
        return departureTimeStr;
    }

    public void setDepartureTimeStr(String departureTimeStr) {
        this.departureTimeStr = departureTimeStr;
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

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Integer getPalletCount() {
        return palletCount;
    }

    public void setPalletCount(Integer palletCount) {
        this.palletCount = palletCount;
    }

    public List<String> getScanBatchCodes() {
        return scanBatchCodes;
    }

    public void setScanBatchCodes(List<String> scanBatchCodes) {
        this.scanBatchCodes = scanBatchCodes;
    }

    public List<String> getScanSealCodes() {
        return scanSealCodes;
    }

    public void setScanSealCodes(List<String> scanSealCodes) {
        this.scanSealCodes = scanSealCodes;
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

    public Double getScanBatchCodeTotalWeight() {
        return scanBatchCodeTotalWeight;
    }

    public void setScanBatchCodeTotalWeight(Double scanBatchCodeTotalWeight) {
        this.scanBatchCodeTotalWeight = scanBatchCodeTotalWeight;
    }

    public Double getScanBatchCodeTotalVolume() {
        return scanBatchCodeTotalVolume;
    }

    public void setScanBatchCodeTotalVolume(Double scanBatchCodeTotalVolume) {
        this.scanBatchCodeTotalVolume = scanBatchCodeTotalVolume;
    }

    public Double getScanBatchCodeTotalItemNum() {
        return scanBatchCodeTotalItemNum;
    }

    public void setScanBatchCodeTotalItemNum(Double scanBatchCodeTotalItemNum) {
        this.scanBatchCodeTotalItemNum = scanBatchCodeTotalItemNum;
    }
}
