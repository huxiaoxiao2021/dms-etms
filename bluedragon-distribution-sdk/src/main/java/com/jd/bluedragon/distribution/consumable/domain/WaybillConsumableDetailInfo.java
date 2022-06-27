package com.jd.bluedragon.distribution.consumable.domain;

import java.math.BigDecimal;

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

    /** 包装耗材价格 **/
    private BigDecimal packingCharge;

    /** 打包后包装体积（立方米） **/
    private Double confirmVolume;

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

    public BigDecimal getPackingCharge() {
        return packingCharge;
    }

    public void setPackingCharge(BigDecimal packingCharge) {
        this.packingCharge = packingCharge;
    }

    public Double getConfirmVolume() {
        return confirmVolume;
    }

    public void setConfirmVolume(Double confirmVolume) {
        this.confirmVolume = confirmVolume;
    }
}
