package com.jd.bluedragon.common.dto.operation.workbench.unload.response;

import java.io.Serializable;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.common.dto.operation.workbench.unload.response
 * @Description:
 * @date Date : 2024年01月19日 14:22
 */
public class UnLoadScanResponse implements Serializable {

    private static final long serialVersionUID = -1410653337122346470L;

    /**
     * 本次扫描的包裹数
     */
    private Integer scanPackCount;
    /**
     * 本次扫描的运单下包裹总数
     */
    private Integer scanWaybillPackSum;

    //======================================

    public Integer getScanPackCount() {
        return scanPackCount;
    }

    public void setScanPackCount(Integer scanPackCount) {
        this.scanPackCount = scanPackCount;
    }

    public Integer getScanWaybillPackSum() {
        return scanWaybillPackSum;
    }

    public void setScanWaybillPackSum(Integer scanWaybillPackSum) {
        this.scanWaybillPackSum = scanWaybillPackSum;
    }
}
