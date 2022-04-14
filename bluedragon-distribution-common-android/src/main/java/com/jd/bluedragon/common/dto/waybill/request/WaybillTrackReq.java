package com.jd.bluedragon.common.dto.waybill.request;

import java.io.Serializable;

public class WaybillTrackReq implements Serializable {

    private String erp;

    private String barCode;

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
