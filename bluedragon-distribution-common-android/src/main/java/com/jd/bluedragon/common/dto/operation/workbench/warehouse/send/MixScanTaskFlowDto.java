package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.comboard.response.BoardDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @description
 * @date 2023-05-16 14:37
 */
public class MixScanTaskFlowDto {
    private Integer startSiteId;
    private Integer endSiteId;
    private String endSiteName;
    /**
     * 滑道编号
     */
    private String crossCode;
    /**
     * 笼车编号
     */
    private String tableTrolleyCode;
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
     * 关注状态
     */
    private Integer focus;

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
    
    /**
     * 派车明细任务
     */
    private String transWorkItemCode;

    
    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public String getTableTrolleyCode() {
        return tableTrolleyCode;
    }

    public void setTableTrolleyCode(String tableTrolleyCode) {
        this.tableTrolleyCode = tableTrolleyCode;
    }

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

    public Integer getFocus() {
        return focus;
    }

    public void setFocus(Integer focus) {
        this.focus = focus;
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

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }
}
