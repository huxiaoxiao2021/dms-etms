package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liwenji
 * @description
 * @date 2023-05-16 14:37
 */
public class MixScanTaskFlowAgg extends MixScanTaskDetailDto implements Serializable {

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
     * 已扫箱数
     */
    private Long scannedBoxCount = 0L;

    /**
     * 拦截包裹数
     */
    private Long interceptedPackCount = 0L;

    /**
     * 强制发包裹数
     */
    private Long forceSendPackCount = 0L;

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

    public Integer getLoadRateUpperLimit() {
        return loadRateUpperLimit;
    }

    public void setLoadRateUpperLimit(Integer loadRateUpperLimit) {
        this.loadRateUpperLimit = loadRateUpperLimit;
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

}
