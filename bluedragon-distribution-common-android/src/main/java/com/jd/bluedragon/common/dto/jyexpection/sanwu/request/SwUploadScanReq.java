package com.jd.bluedragon.common.dto.jyexpection.sanwu.request;

public class SwUploadScanReq extends SanwuReq {

    private String barCode;

    private String source;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
