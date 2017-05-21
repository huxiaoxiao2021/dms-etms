package com.jd.bluedragon.distribution.api.domain;

/**
 * Created by zhanglei51 on 2017/5/10.
 */
public class NewSealVehicleBean {

    /**运力编码*/
    private String capacityCode;

    /** 发货批次号 */
    private String sendCode;

    /** 重量 */
    private Double weight;

    /** 体积 */
    private Double volume;

    public String getCapacityCode() {
        return capacityCode;
    }

    public void setCapacityCode(String capacityCode) {
        this.capacityCode = capacityCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
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
}
