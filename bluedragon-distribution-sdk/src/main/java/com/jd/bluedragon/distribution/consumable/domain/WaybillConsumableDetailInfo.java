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
    private Double receiveQuantity;

    /** 确认数量 */
    private Double confirmQuantity;

    /** 包装人ERP */
    private String packUserErp;

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

    public Double getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(Double receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public Double getConfirmQuantity() {
        return confirmQuantity;
    }

    public void setConfirmQuantity(Double confirmQuantity) {
        this.confirmQuantity = confirmQuantity;
    }

    public String getPackUserErp() {
        return packUserErp;
    }

    public void setPackUserErp(String packUserErp) {
        this.packUserErp = packUserErp;
    }
}
