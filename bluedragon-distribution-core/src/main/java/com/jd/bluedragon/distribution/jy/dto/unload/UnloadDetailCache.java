package com.jd.bluedragon.distribution.jy.dto.unload;

import com.jd.bluedragon.distribution.jy.annotation.RedisHashColumn;
import com.jd.bluedragon.distribution.jy.constants.RedisHashKeyConstants;

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
    @RedisHashColumn(hashField = RedisHashKeyConstants.UNLOAD_TOTAL_COUNT)
    private Integer totalCount;

    /**
     * 已卸包裹总数
     */
    @RedisHashColumn(hashField = RedisHashKeyConstants.UNLOAD_COUNT)
    private Integer unloadCount;

    /**
     * 拦截应扫数量
     */
    @RedisHashColumn(hashField = RedisHashKeyConstants.UNLOAD_INTERCEPT_SHOULD_SCAN_COUNT)
    private Integer interceptShouldScanCount;

    /**
     * 拦截已扫数量
     */
    @RedisHashColumn(hashField = RedisHashKeyConstants.UNLOAD_INTERCEPT_ACTUAL_SCAN_COUNT)
    private Integer interceptActualScanCount;

    /**
     * 本场地多扫数量
     */
    @RedisHashColumn(hashField = RedisHashKeyConstants.UNLOAD_MORE_SCAN_LOCAL_COUNT)
    private Integer moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    @RedisHashColumn(hashField = RedisHashKeyConstants.UNLOAD_MORE_SCAN_OUT_COUNT)
    private Integer moreScanOutCount;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getUnloadCount() {
        return unloadCount;
    }

    public void setUnloadCount(Integer unloadCount) {
        this.unloadCount = unloadCount;
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

    public Integer getMoreScanLocalCount() {
        return moreScanLocalCount;
    }

    public void setMoreScanLocalCount(Integer moreScanLocalCount) {
        this.moreScanLocalCount = moreScanLocalCount;
    }

    public Integer getMoreScanOutCount() {
        return moreScanOutCount;
    }

    public void setMoreScanOutCount(Integer moreScanOutCount) {
        this.moreScanOutCount = moreScanOutCount;
    }
}
