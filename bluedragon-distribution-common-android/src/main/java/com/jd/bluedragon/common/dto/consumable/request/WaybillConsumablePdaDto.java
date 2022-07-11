package com.jd.bluedragon.common.dto.consumable.request;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210621
 **/
public class WaybillConsumablePdaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 运单号 */
    private String waybillCode;

    /** 耗材编号 */
    private String consumableCode;

    /** 确认数量 */
    private Double confirmQuantity;

    /** 确认体积 **/
    private Double confirmVolume;

    /** 耗材类型 **/
    private String consumableTypeCode;

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

    public Double getConfirmQuantity() {
        return confirmQuantity;
    }

    public void setConfirmQuantity(Double confirmQuantity) {
        this.confirmQuantity = confirmQuantity;
    }

    public Double getConfirmVolume() {
        return confirmVolume;
    }

    public void setConfirmVolume(Double confirmVolume) {
        this.confirmVolume = confirmVolume;
    }

    public String getConsumableTypeCode() {
        return consumableTypeCode;
    }

    public void setConsumableTypeCode(String consumableTypeCode) {
        this.consumableTypeCode = consumableTypeCode;
    }
}
