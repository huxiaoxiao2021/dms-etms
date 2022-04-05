package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.distribution.jy.annotation.RedisHashColumn;

import java.io.Serializable;

/**
 * @ClassName UnloadDetailCache
 * @Description
 * @Author wyh
 * @Date 2022/4/5 13:56
 **/
public class UnloadDetailCache implements Serializable {

    private static final long serialVersionUID = -610708170755930275L;

    /**
     * 卸车任务业务主键
     */
    @RedisHashColumn(hashField = "bizId")
    private String bizId;

    /**
     * 应卸包裹总数
     */
    @RedisHashColumn(hashField = "totalCount")
    private Long totalCount;

    /**
     * 已卸包裹总数
     */
    @RedisHashColumn(hashField = "unloadCount")
    private Long unloadCount;

    /**
     * 拦截应扫数量
     */
    @RedisHashColumn(hashField = "interceptShouldScanCount")
    private Long interceptShouldScanCount;

    /**
     * 拦截已扫数量
     */
    @RedisHashColumn(hashField = "interceptActualScanCount")
    private Long interceptActualScanCount;

    /**
     * 本场地多扫数量
     */
    @RedisHashColumn(hashField = "moreScanLocalCount")
    private Long moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    @RedisHashColumn(hashField = "moreScanOutCount")
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
