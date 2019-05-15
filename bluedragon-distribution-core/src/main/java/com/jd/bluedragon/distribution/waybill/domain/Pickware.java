package com.jd.bluedragon.distribution.waybill.domain;

public class Pickware {
    
    /** 取件单内商品名称 */
    private String productName;
    
    /** 取件单内商品编号 */
    private String productCode;
    
    /** 取件单内商品数量 */
    private Integer quantity;
    
    /** 取件单内商品附件 */
    private String accessory;
    
    /** 取件单编号 */
    private String code;
    
    /** 取件单对应运单编号 */
    private String waybillCode;
    
    public String getProductName() {
        return this.productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductCode() {
        return this.productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public Integer getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getAccessory() {
        return this.accessory;
    }
    
    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String toString() {
        return "Pickware [productName=" + this.productName + ", productCode=" + this.productCode
                + ", quantity=" + this.quantity + ", accessory=" + this.accessory + ", code="
                + this.code + ", waybillCode=" + this.waybillCode + "]";
    }
    
}
