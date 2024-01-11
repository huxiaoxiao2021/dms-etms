package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 
 * @date 2023-08-21 17:29
 */
public class SendScanBarCodeDto implements Serializable {

    /**
     * 包裹号|箱号
     */
    private String barCode;

    /**
     * 包裹数量
     */
    private Long packCount;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Long getPackCount() {
        return packCount;
    }

    public void setPackCount(Long packCount) {
        this.packCount = packCount;
    }
}
