package com.jd.bluedragon.distribution.sendCode.domain;

/**
 * <p>
 *
 * @author zoothon
 * @since 2020/2/18
 **/
public class HugeSendCodeEntity {

    private String SendCode;

    private Double weight;

    private Double volume;

    private Boolean isFreshCode;

    public String getSendCode() {
        return SendCode;
    }

    public void setSendCode(String sendCode) {
        SendCode = sendCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Boolean getFreshCode() {
        return isFreshCode;
    }

    public void setFreshCode(Boolean freshCode) {
        isFreshCode = freshCode;
    }
}
