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
     * 卸车任务业务主键
     */
    private String bizId;

    /**
     * 应卸包裹总数
     */
    private Long totalCount;

    /**
     * 已卸包裹总数
     */
    private Long unloadCount;

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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getUnloadCount() {
        return unloadCount;
    }

    public void setUnloadCount(Long unloadCount) {
        this.unloadCount = unloadCount;
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
