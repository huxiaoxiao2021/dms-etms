package com.jd.bluedragon.distribution.ky.domain;

import java.io.Serializable;

public class KYPrintInfo implements Serializable {

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 与批次号对应的KY单号
     */
    private String kyCode;

    /**
     * 目的航空站点
     */
    private String receiveAirPortName;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getKyCode() {
        return kyCode;
    }

    public void setKyCode(String kyCode) {
        this.kyCode = kyCode;
    }

    public String getReceiveAirPortName() {
        return receiveAirPortName;
    }

    public void setReceiveAirPortName(String receiveAirPortName) {
        this.receiveAirPortName = receiveAirPortName;
    }
}
