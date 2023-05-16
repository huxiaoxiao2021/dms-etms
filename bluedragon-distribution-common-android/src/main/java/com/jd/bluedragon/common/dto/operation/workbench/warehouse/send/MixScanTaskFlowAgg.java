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
     * 该流向拦截数量
     */
    private Integer interceptCount;

    /**
     * 该流向已扫包裹数量
     */
    private Integer packageHaveScanCount;
    /**
     * 该流向已扫箱子数量
     */
    private Integer boxHaveScanCount;
    /**
     * 该流向待扫数量
     */
    private Integer waitScanCount;
    
    /**
     * 关注数量
     */
    private Integer focusCount;
    
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
    

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getPackageHaveScanCount() {
        return packageHaveScanCount;
    }

    public void setPackageHaveScanCount(Integer packageHaveScanCount) {
        this.packageHaveScanCount = packageHaveScanCount;
    }

    public Integer getBoxHaveScanCount() {
        return boxHaveScanCount;
    }

    public void setBoxHaveScanCount(Integer boxHaveScanCount) {
        this.boxHaveScanCount = boxHaveScanCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }

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

    public Integer getFocusCount() {
        return focusCount;
    }

    public void setFocusCount(Integer focusCount) {
        this.focusCount = focusCount;
    }

}
