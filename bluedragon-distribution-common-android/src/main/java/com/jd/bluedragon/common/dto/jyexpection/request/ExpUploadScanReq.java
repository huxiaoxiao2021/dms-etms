package com.jd.bluedragon.common.dto.jyexpection.request;

public class ExpUploadScanReq extends ExpBaseReq {

    private String barCode;

    private Integer source;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }
}
