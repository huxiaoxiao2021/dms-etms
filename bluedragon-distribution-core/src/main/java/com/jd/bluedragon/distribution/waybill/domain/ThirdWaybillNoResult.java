package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;

public class ThirdWaybillNoResult implements Serializable {

    private static final long serialVersionUID = -2011227775577083531L;
    //包裹号
    private String waybillCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    @Override
    public String toString() {
        return "ThirdWaybillNoResult{" +
                "waybillCode='" + waybillCode + '\'' +
                '}';
    }
}
