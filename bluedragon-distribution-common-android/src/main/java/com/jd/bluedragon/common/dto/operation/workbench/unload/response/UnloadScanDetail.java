package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;

/**
 * @ClassName UnloadScanDetail
 * @Description
 * @Author wyh
 * @Date 2022/3/31 20:59
 **/
public class UnloadScanDetail implements Serializable {

    private static final long serialVersionUID = -7823254733387066564L;

    /**
     * 应扫数量
     */
    private Long shouldScanCount;

    /**
     * 已扫数量
     */
    private Long actualScanCount;

    /**
     * 拦截应扫数量
     */
    private Long interceptShouldScanCount;

    /**
     * 拦截已扫数量
     */
    private Long interceptActualScanCount;

    /**
     * 本场地多扫数量
     */
    private Long moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    private Long moreScanOutCount;

    public Long getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Long shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Long getActualScanCount() {
        return actualScanCount;
    }

    public void setActualScanCount(Long actualScanCount) {
        this.actualScanCount = actualScanCount;
    }

    public Long getInterceptShouldScanCount() {
        return interceptShouldScanCount;
    }

    public void setInterceptShouldScanCount(Long interceptShouldScanCount) {
        this.interceptShouldScanCount = interceptShouldScanCount;
    }

    public Long getInterceptActualScanCount() {
        return interceptActualScanCount;
    }

    public void setInterceptActualScanCount(Long interceptActualScanCount) {
        this.interceptActualScanCount = interceptActualScanCount;
    }

    public Long getMoreScanLocalCount() {
        return moreScanLocalCount;
    }

    public void setMoreScanLocalCount(Long moreScanLocalCount) {
        this.moreScanLocalCount = moreScanLocalCount;
    }

    public Long getMoreScanOutCount() {
        return moreScanOutCount;
    }

    public void setMoreScanOutCount(Long moreScanOutCount) {
        this.moreScanOutCount = moreScanOutCount;
    }
}
