package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response;

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
    private Long interceptScanCount;

    /**
     * 明细列表
     */
    private List<InspectionScanBarCode> barCodeList;

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

    public Long getInterceptScanCount() {
        return interceptScanCount;
    }

    public void setInterceptScanCount(Long interceptScanCount) {
        this.interceptScanCount = interceptScanCount;
    }

    public List<InspectionScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<InspectionScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
