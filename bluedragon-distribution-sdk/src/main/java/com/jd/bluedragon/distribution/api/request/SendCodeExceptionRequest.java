package com.jd.bluedragon.distribution.api.request;

import java.util.List;

public class SendCodeExceptionRequest {

    /**
     * 单号
     */
    private String barCode;

    /**
     * 类型 ：1全部；2已操作；3未操作
     */
    private Integer type;

    /**
     * 批次号列表
     */
    private List<String> sendCodes;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }
}
