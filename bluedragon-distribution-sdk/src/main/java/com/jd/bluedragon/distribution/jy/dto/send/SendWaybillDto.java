package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

public class SendWaybillDto implements Serializable {

    private static final long serialVersionUID = 7642315943849772204L;
    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 运单下全量包裹数量
     */
    private Long allPackCount;

    /**
     * 异常类型对应扫描包裹数量
     */
    private Long scanPackCount;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Long getAllPackCount() {
        return allPackCount;
    }

    public void setAllPackCount(Long allPackCount) {
        this.allPackCount = allPackCount;
    }

    public Long getScanPackCount() {
        return scanPackCount;
    }

    public void setScanPackCount(Long scanPackCount) {
        this.scanPackCount = scanPackCount;
    }
}
