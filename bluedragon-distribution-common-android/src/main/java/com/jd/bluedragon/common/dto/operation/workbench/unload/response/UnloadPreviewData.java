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
     * 拦截已扫数量
     */
    private Long interceptActualScanCount;

    /**
     * 拦截未扫
     */
    private Long interceptNotScanCount;

    /**
     * 待扫描数量
     */
    private Long shouldScanCount;

    /**
     * 本场地多扫数量
     */
    private Long moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    private Long moreScanOutCount;

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

    public Long getInterceptActualScanCount() {
        return interceptActualScanCount;
    }

    public void setInterceptActualScanCount(Long interceptActualScanCount) {
        this.interceptActualScanCount = interceptActualScanCount;
    }

    public Long getInterceptNotScanCount() {
        return interceptNotScanCount;
    }

    public void setInterceptNotScanCount(Long interceptNotScanCount) {
        this.interceptNotScanCount = interceptNotScanCount;
    }

    public Long getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Long shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
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

    public List<UnloadScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<UnloadScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
