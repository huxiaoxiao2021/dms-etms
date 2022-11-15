package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

/**
 * 扫描统计数据
 */
public class StatisticsDto implements Serializable {

    /**
     * 卸车进度
     */
    private Integer processPercent;
    /**
     * 拦截数量
     */
    private Integer interceptCount;
    /**
     * 多扫数量
     */
    private Integer extraScanCount;

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
     * 运单数量
     */
    private Integer waybillCount;


    public Integer getProcessPercent() {
        return processPercent;
    }

    public void setProcessPercent(Integer processPercent) {
        this.processPercent = processPercent;
    }

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getExtraScanCount() {
        return extraScanCount;
    }

    public void setExtraScanCount(Integer extraScanCount) {
        this.extraScanCount = extraScanCount;
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

    public Integer getWaybillCount() {
        return waybillCount;
    }

    public void setWaybillCount(Integer waybillCount) {
        this.waybillCount = waybillCount;
    }
}
