package com.jd.bluedragon.distribution.consumable.domain;

/**
 * Created by hanjiaxing1 on 2018/8/23.
 */
public class WaybillConsumableDetailInfo extends PackingConsumableInfo {

    /** 运单号 */
    private String waybillCode;

    /** 耗材编号 */
    private String consumableCode;

    /** 揽收数量 */
    private Integer receiveQuantity;

    /** 确认数量 */
    private Integer confirmQuantity;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getConsumableCode() {
        return consumableCode;
    }

    public void setConsumableCode(String consumableCode) {
        this.consumableCode = consumableCode;
    }

    public Integer getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(Integer receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public Integer getConfirmQuantity() {
        return confirmQuantity;
    }

    public void setConfirmQuantity(Integer confirmQuantity) {
        this.confirmQuantity = confirmQuantity;
    }
}
