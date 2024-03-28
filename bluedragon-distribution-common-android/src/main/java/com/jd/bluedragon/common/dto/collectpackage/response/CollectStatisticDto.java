package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

public class CollectStatisticDto implements Serializable {
    private static final long serialVersionUID = 3190684843428732333L;

    private Integer totalScanCount;
    private Integer packageScanCount;
    private Integer boxScanCount;

    private Integer totalInterceptCount;
    private Integer packageInterceptCount;
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
