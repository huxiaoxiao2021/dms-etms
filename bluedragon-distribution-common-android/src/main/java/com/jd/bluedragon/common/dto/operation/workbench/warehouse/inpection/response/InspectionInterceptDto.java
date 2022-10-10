package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response;

import java.io.Serializable;
import java.util.List;

/**
 * 拦截包裹明细
 * @author fanggang7
 * @time 2022-10-09 14:43:56 周日
 */
public class InspectionInterceptDto implements Serializable {

    private static final long serialVersionUID = 5898441268950619129L;

    /**
     * 已扫数量
     */
    private Long actualScanCount;

    /**
     * 包裹列表
     */
    private List<InspectionScanBarCode> barcodeList;

    public Long getActualScanCount() {
        return actualScanCount;
    }

    public void setActualScanCount(Long actualScanCount) {
        this.actualScanCount = actualScanCount;
    }

    public List<InspectionScanBarCode> getBarcodeList() {
        return barcodeList;
    }

    public void setBarcodeList(List<InspectionScanBarCode> barcodeList) {
        this.barcodeList = barcodeList;
    }
}
