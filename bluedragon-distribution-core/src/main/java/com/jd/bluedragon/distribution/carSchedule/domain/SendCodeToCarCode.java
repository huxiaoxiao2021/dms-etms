package com.jd.bluedragon.distribution.carSchedule.domain;

/**
 * Created by wuzuxiang on 2017/3/13.
 */
public class SendCodeToCarCode {

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 发车条码
     */
    private String sendCarCode;

    /**
     * 是否有效
     */
    private Integer yn;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getSendCarCode() {
        return sendCarCode;
    }

    public void setSendCarCode(String sendCarCode) {
        this.sendCarCode = sendCarCode;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
