package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

/**
 * 扫描统计数据
 */
public class ScanStatisticsDto implements Serializable {
    private static final long serialVersionUID = 8179121445309123824L;
    /**
     * 卸车进度
     */
    private Integer processPercent;

    /**
     * 应扫
     */
    private Integer shouldScanCount;

    /**
     * 待扫
     */
    private Integer waitScanCount;

    /**
     * 已扫
     */
    private Integer haveScanCount;

    /**
     * 拦截应扫数量
     */
    private Integer interceptShouldScanCount;
    /**
     * 拦截已扫数量
     */
    private Integer interceptActualScanCount;
    /**
     * 多扫数量本场地
     */
    private Integer extraScanCountCurrSite;

    /**
     * 多扫数量非本场地
     */
    private Integer extraScanCountOutCurrSite;

    public Integer getProcessPercent() {
        return processPercent;
    }

    public void setProcessPercent(Integer processPercent) {
        this.processPercent = processPercent;
    }

    public Integer getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Integer shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }

    public Integer getHaveScanCount() {
        return haveScanCount;
    }

    public void setHaveScanCount(Integer haveScanCount) {
        this.haveScanCount = haveScanCount;
    }

    public Integer getInterceptShouldScanCount() {
        return interceptShouldScanCount;
    }

    public void setInterceptShouldScanCount(Integer interceptShouldScanCount) {
        this.interceptShouldScanCount = interceptShouldScanCount;
    }

    public Integer getInterceptActualScanCount() {
        return interceptActualScanCount;
    }

    public void setInterceptActualScanCount(Integer interceptActualScanCount) {
        this.interceptActualScanCount = interceptActualScanCount;
    }

    public Integer getExtraScanCountCurrSite() {
        return extraScanCountCurrSite;
    }

    public void setExtraScanCountCurrSite(Integer extraScanCountCurrSite) {
        this.extraScanCountCurrSite = extraScanCountCurrSite;
    }

    public Integer getExtraScanCountOutCurrSite() {
        return extraScanCountOutCurrSite;
    }

    public void setExtraScanCountOutCurrSite(Integer extraScanCountOutCurrSite) {
        this.extraScanCountOutCurrSite = extraScanCountOutCurrSite;
    }
}
