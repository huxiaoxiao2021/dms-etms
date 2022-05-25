package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class ThirdWaybillNoWyDto implements Serializable {

    private static final long serialVersionUID = 7762830393431460039L;
    private String waybillCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    @Override
    public String toString() {
        return "ThirdWaybillNoAndroidDto{" +
                "waybillCode='" + waybillCode + '\'' +
                '}';
    }
}
