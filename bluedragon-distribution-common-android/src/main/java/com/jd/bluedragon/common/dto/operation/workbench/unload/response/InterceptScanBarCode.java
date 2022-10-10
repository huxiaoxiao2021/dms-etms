package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName InterceptScanBarCode
 * @Description 卸车拦截扫描明细
 * @Author wyh
 * @Date 2022/4/1 15:22
 **/
public class InterceptScanBarCode implements Serializable {

    private static final long serialVersionUID = 5898441268950619129L;

    /**
     * 已扫数量
     */
    private Long actualScanCount;

    /**
     * 应扫数量
     */
    private Long shouldScanCount;

    /**
     * 包裹列表
     */
    private List<UnloadScanBarCode> barCodeList;

    public Long getActualScanCount() {
        return actualScanCount;
    }

    public void setActualScanCount(Long actualScanCount) {
        this.actualScanCount = actualScanCount;
    }

    public Long getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Long shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public List<UnloadScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<UnloadScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
