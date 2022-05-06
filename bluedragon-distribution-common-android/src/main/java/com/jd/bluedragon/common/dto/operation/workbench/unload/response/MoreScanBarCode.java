package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName InterceptScanBarCode
 * @Description 卸车多扫明细
 * @Author wyh
 * @Date 2022/4/1 15:22
 **/
public class MoreScanBarCode implements Serializable {

    private static final long serialVersionUID = 1989217376332344021L;

    /**
     * 本场地多扫数量
     */
    private Long moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    private Long moreScanOutCount;

    /**
     * 包裹列表
     */
    private List<UnloadScanBarCode> barCodeList;

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
