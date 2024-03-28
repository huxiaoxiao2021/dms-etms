package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

/**
 * @author weixiaofeng12
 * @description 集包、箱统计指标
 * @date 2024-03-11 17:56
 */
public class CollectStatisticDto implements Serializable {
    private static final long serialVersionUID = 3190684843428732333L;

    /**
     * 已扫总数
     */
    private Integer totalScanCount;
    /**
     * 包裹已扫数
     */
    private Integer packageScanCount;
    /**
     * 箱号已扫数
     */
    private Integer boxScanCount;

    /**
     * 拦截总数
     */
    private Integer totalInterceptCount;
    /**
     * 包裹拦截数
     */
    private Integer packageInterceptCount;
    /**
     * 箱拦截数
     */
    private Integer boxInterceptCount;

    public Integer getTotalScanCount() {
        return totalScanCount;
    }

    public void setTotalScanCount(Integer totalScanCount) {
        this.totalScanCount = totalScanCount;
    }

    public Integer getPackageScanCount() {
        return packageScanCount;
    }

    public void setPackageScanCount(Integer packageScanCount) {
        this.packageScanCount = packageScanCount;
    }

    public Integer getBoxScanCount() {
        return boxScanCount;
    }

    public void setBoxScanCount(Integer boxScanCount) {
        this.boxScanCount = boxScanCount;
    }

    public Integer getTotalInterceptCount() {
        return totalInterceptCount;
    }

    public void setTotalInterceptCount(Integer totalInterceptCount) {
        this.totalInterceptCount = totalInterceptCount;
    }

    public Integer getPackageInterceptCount() {
        return packageInterceptCount;
    }

    public void setPackageInterceptCount(Integer packageInterceptCount) {
        this.packageInterceptCount = packageInterceptCount;
    }

    public Integer getBoxInterceptCount() {
        return boxInterceptCount;
    }

    public void setBoxInterceptCount(Integer boxInterceptCount) {
        this.boxInterceptCount = boxInterceptCount;
    }
}
