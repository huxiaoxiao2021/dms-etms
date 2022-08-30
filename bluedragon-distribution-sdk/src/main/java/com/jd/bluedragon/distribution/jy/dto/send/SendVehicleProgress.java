package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName SendVehicleProgress
 * @Description
 * @Author wyh
 * @Date 2022/5/19 16:16
 **/
public class SendVehicleProgress implements Serializable {

    private static final long serialVersionUID = -7310020588075926827L;

    /**
     * 重量装载率 100%
     */
    private BigDecimal loadRate = BigDecimal.ZERO;

    /**
     * 车型体积 单位：立方米
     */
    private BigDecimal volume = BigDecimal.ZERO;

    /**
     * 车型重量 单位：吨
     */
    private BigDecimal weight = BigDecimal.ZERO;

    /**
     * 装载体积 单位：立方厘米
     */
    private BigDecimal loadVolume = BigDecimal.ZERO;

    /**
     * 装载重量 单位：千克
     */
    private BigDecimal loadWeight = BigDecimal.ZERO;

    /**
     * 装载率上限
     */
    private Integer loadRateUpperLimit;

    /**
     * 待扫包裹数
     */
    private Long toScanCount = 0L;

    /**
     * 已扫包裹数
     */
    private Long scannedPackCount = 0L;

    /**
     * 待扫运单数
     */
    private Long toScanWaybillCount = 0L;

    /**
     * 已扫运单数
     */
    private Long scannedWaybillCount = 0L;

    /**
     * 不齐运单数
     */
    private Long incompleteWaybillCount = 0L;


    /**
     * 已扫箱数
     */
    private Long scannedBoxCount = 0L;

    /**
     * 拦截包裹数
     */
    private Long interceptedPackCount = 0L;

    /**
     * 拦截运单数
     */
    private Long interceptedWaybillCount = 0L;

    /**
     * 强制发包裹数
     */
    private Long forceSendPackCount = 0L;

    /**
     * 封车的流向数量
     */
    private Integer sealedTotal = 0;

    /**
     * 流向总数
     */
    private Integer destTotal = 0;

    public BigDecimal getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLoadVolume() {
        return loadVolume;
    }

    public void setLoadVolume(BigDecimal loadVolume) {
        this.loadVolume = loadVolume;
    }

    public BigDecimal getLoadWeight() {
        return loadWeight;
    }

    public void setLoadWeight(BigDecimal loadWeight) {
        this.loadWeight = loadWeight;
    }

    public Long getToScanCount() {
        return toScanCount;
    }

    public void setToScanCount(Long toScanCount) {
        this.toScanCount = toScanCount;
    }

    public Long getScannedPackCount() {
        return scannedPackCount;
    }

    public void setScannedPackCount(Long scannedPackCount) {
        this.scannedPackCount = scannedPackCount;
    }

    public Long getScannedBoxCount() {
        return scannedBoxCount;
    }

    public void setScannedBoxCount(Long scannedBoxCount) {
        this.scannedBoxCount = scannedBoxCount;
    }

    public Long getInterceptedPackCount() {
        return interceptedPackCount;
    }

    public void setInterceptedPackCount(Long interceptedPackCount) {
        this.interceptedPackCount = interceptedPackCount;
    }

    public Long getForceSendPackCount() {
        return forceSendPackCount;
    }

    public void setForceSendPackCount(Long forceSendPackCount) {
        this.forceSendPackCount = forceSendPackCount;
    }

    public Integer getSealedTotal() {
        return sealedTotal;
    }

    public void setSealedTotal(Integer sealedTotal) {
        this.sealedTotal = sealedTotal;
    }

    public Integer getDestTotal() {
        return destTotal;
    }

    public void setDestTotal(Integer destTotal) {
        this.destTotal = destTotal;
    }

    public Integer getLoadRateUpperLimit() {
        return loadRateUpperLimit;
    }

    public void setLoadRateUpperLimit(Integer loadRateUpperLimit) {
        this.loadRateUpperLimit = loadRateUpperLimit;
    }

    public Long getToScanWaybillCount() {
        return toScanWaybillCount;
    }

    public void setToScanWaybillCount(Long toScanWaybillCount) {
        this.toScanWaybillCount = toScanWaybillCount;
    }

    public Long getScannedWaybillCount() {
        return scannedWaybillCount;
    }

    public void setScannedWaybillCount(Long scannedWaybillCount) {
        this.scannedWaybillCount = scannedWaybillCount;
    }

    public Long getIncompleteWaybillCount() {
        return incompleteWaybillCount;
    }

    public void setIncompleteWaybillCount(Long incompleteWaybillCount) {
        this.incompleteWaybillCount = incompleteWaybillCount;
    }

    public Long getInterceptedWaybillCount() {
        return interceptedWaybillCount;
    }

    public void setInterceptedWaybillCount(Long interceptedWaybillCount) {
        this.interceptedWaybillCount = interceptedWaybillCount;
    }
}
