package com.jd.bluedragon.common.dto.consumable.response;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210617
 **/
public class WaybillConsumablePackConfirmRes implements Serializable {

    private static final long serialVersionUID = 1L;


    private String waybillCode;
    /** 实发场地 */
    private String dmsName;
    /** 耗材揽收数量 */
    private Double receiveQuantity;
    /** 耗材名称 */
    private String consumableName;
    /** 耗材类型名称 */
    private String consumableTypeName;
    /**耗材编号、*/
    private String consumableCode;
    /** 耗材类型编码 **/
    private String consumableTypeCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getDmsName() {
        return dmsName;
    }

    public void setDmsName(String dmsName) {
        this.dmsName = dmsName;
    }

    public Double getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(Double receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public String getConsumableName() {
        return consumableName;
    }

    public void setConsumableName(String consumableName) {
        this.consumableName = consumableName;
    }

    public String getConsumableTypeName() {
        return consumableTypeName;
    }

    public void setConsumableTypeName(String consumableTypeName) {
        this.consumableTypeName = consumableTypeName;
    }

    public String getConsumableCode() {
        return consumableCode;
    }

    public void setConsumableCode(String consumableCode) {
        this.consumableCode = consumableCode;
    }

    public String getConsumableTypeCode() {
        return consumableTypeCode;
    }

    public void setConsumableTypeCode(String consumableTypeCode) {
        this.consumableTypeCode = consumableTypeCode;
    }
}
