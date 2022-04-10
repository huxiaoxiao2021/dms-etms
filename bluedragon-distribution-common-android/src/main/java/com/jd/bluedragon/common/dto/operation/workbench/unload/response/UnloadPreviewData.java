package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UnloadPreviewData
 * @Description 卸车扫描完成预览数据
 * @Author wyh
 * @Date 2022/3/31 23:35
 **/
public class UnloadPreviewData implements Serializable {

    private static final long serialVersionUID = -546411604277439598L;

    /**
     * 是否异常；0-否 1-是
     */
    private Byte abnormalFlag;

    /**
     * 总扫描数量
     */
    private Long totalScan;

    /**
     * 异常数量
     */
    private Long abnormalCount;

    /**
     * 拦截数量
     */
    private Long interceptCount;

    /**
     * 待扫描数量
     */
    private Long shouldScanCount;

    /**
     * 多扫数量
     */
    private Long moreScanCount;

    /**
     * 待扫和多扫明细
     */
    private List<UnloadScanBarCode> barCodeList;

    public Byte getAbnormalFlag() {
        return abnormalFlag;
    }

    public void setAbnormalFlag(Byte abnormalFlag) {
        this.abnormalFlag = abnormalFlag;
    }

    public Long getTotalScan() {
        return totalScan;
    }

    public void setTotalScan(Long totalScan) {
        this.totalScan = totalScan;
    }

    public Long getAbnormalCount() {
        return abnormalCount;
    }

    public void setAbnormalCount(Long abnormalCount) {
        this.abnormalCount = abnormalCount;
    }

    public Long getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Long interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Long getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Long shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Long getMoreScanCount() {
        return moreScanCount;
    }

    public void setMoreScanCount(Long moreScanCount) {
        this.moreScanCount = moreScanCount;
    }

    public List<UnloadScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<UnloadScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
