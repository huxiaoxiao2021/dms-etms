package com.jd.bluedragon.distribution.waybill.domain;

/**
 * @author : xumigen
 * @date : 2019/9/29
 */
public class CancelFeatherLetterRequest {

    private String waybillCode;

    /**
     * true 取消，false 不取消
     */
    private Boolean cancelFeatherLetter;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Boolean getCancelFeatherLetter() {
        return cancelFeatherLetter;
    }

    public void setCancelFeatherLetter(Boolean cancelFeatherLetter) {
        this.cancelFeatherLetter = cancelFeatherLetter;
    }
}
