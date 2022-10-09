package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanBarCode;

import java.io.Serializable;
import java.util.List;

/**
 * 验货完成预览数据
 * @author fanggang7
 * @time 2022-10-09 14:43:56 周日
 */
public class InspectionFinishPreviewData implements Serializable {

    private static final long serialVersionUID = -546411604277439598L;

    /**
     * 是否异常；0-否 1-是
     */
    private Boolean abnormalFlag;

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
     * 明细列表
     */
    private List<UnloadScanBarCode> barCodeList;

    public Boolean getAbnormalFlag() {
        return abnormalFlag;
    }

    public void setAbnormalFlag(Boolean abnormalFlag) {
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

    public List<UnloadScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<UnloadScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
