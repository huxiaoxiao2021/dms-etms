package com.jd.bluedragon.distribution.send.domain;

import java.math.BigDecimal;

/**
 * 包裹信息
 */
public class PackInfo {
    
    /** 订单ID */
    private String orderId;
    
    /** 包裹号 */
    private String packNo;
    
    /** 包裹重量 */
    private BigDecimal packWeight;
    
    /** 包裹体积 */
    private BigDecimal packVolume;
    
    private String orgId;
    private String seqno;
    private String expNo;
    private Integer packNum;
    private String packWkno;
    private Integer orderType;
    private String businessName;
    private String packageSn;
    private Integer status;
    private Integer yn;
    private String siteId;
    private String packDate;
    private Integer priority;
    private Integer sendType;
    private String custNo;
    private String addr;
    private String wareNo;
    private String dmsNo;
    private Integer reType;
    private String expType;
    private Integer flag;
    private String downDate;
    private BigDecimal price = BigDecimal.valueOf(0);
    private BigDecimal shouldPay = BigDecimal.valueOf(0);
    private Integer paymentType = 0;
    private String clientName = "";
    private String clientTel = "";
    private BigDecimal weight = BigDecimal.valueOf(0);
    private BigDecimal volume = BigDecimal.valueOf(0);
    private String weightUnit;
    private String volumeUnit;
    
    public BigDecimal getWeight() {
        return this.weight;
    }
    
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public BigDecimal getVolume() {
        return this.volume;
    }
    
    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
    
    public String getWeightUnit() {
        return this.weightUnit;
    }
    
    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }
    
    public String getVolumeUnit() {
        return this.volumeUnit;
    }
    
    public void setVolumeUnit(String volumeUnit) {
        this.volumeUnit = volumeUnit;
    }
    
    public BigDecimal getPrice() {
        return this.price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getShouldPay() {
        return this.shouldPay;
    }
    
    public void setShouldPay(BigDecimal shouldPay) {
        this.shouldPay = shouldPay;
    }
    
    public Integer getPaymentType() {
        return this.paymentType;
    }
    
    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }
    
    public String getClientName() {
        return this.clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getClientTel() {
        return this.clientTel;
    }
    
    public void setClientTel(String clientTel) {
        this.clientTel = clientTel;
    }
    
    public String getOrgId() {
        return this.orgId;
    }
    
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    
    public String getSeqno() {
        return this.seqno;
    }
    
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }
    
    public String getExpNo() {
        return this.expNo;
    }
    
    public void setExpNo(String expNo) {
        this.expNo = expNo;
    }
    
    public Integer getPackNum() {
        return this.packNum;
    }
    
    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }
    
    public String getPackWkno() {
        return this.packWkno;
    }
    
    public void setPackWkno(String packWkno) {
        this.packWkno = packWkno;
    }
    
    public Integer getOrderType() {
        return this.orderType;
    }
    
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }
    
    public String getBusinessName() {
        return this.businessName;
    }
    
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    
    public String getPackageSn() {
        return this.packageSn;
    }
    
    public void setPackageSn(String packageSn) {
        this.packageSn = packageSn;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getYn() {
        return this.yn;
    }
    
    public void setYn(Integer yn) {
        this.yn = yn;
    }
    
    public String getSiteId() {
        return this.siteId;
    }
    
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    
    public String getPackDate() {
        return this.packDate;
    }
    
    public void setPackDate(String packDate) {
        this.packDate = packDate;
    }
    
    public Integer getPriority() {
        return this.priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public Integer getSendType() {
        return this.sendType;
    }
    
    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }
    
    public String getCustNo() {
        return this.custNo;
    }
    
    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }
    
    public String getAddr() {
        return this.addr;
    }
    
    public void setAddr(String addr) {
        this.addr = addr;
    }
    
    public String getWareNo() {
        return this.wareNo;
    }
    
    public void setWareNo(String wareNo) {
        this.wareNo = wareNo;
    }
    
    public String getDmsNo() {
        return this.dmsNo;
    }
    
    public void setDmsNo(String dmsNo) {
        this.dmsNo = dmsNo;
    }
    
    public Integer getReType() {
        return this.reType;
    }
    
    public void setReType(Integer reType) {
        this.reType = reType;
    }
    
    public String getExpType() {
        return this.expType;
    }
    
    public void setExpType(String expType) {
        this.expType = expType;
    }
    
    public Integer getFlag() {
        return this.flag;
    }
    
    public void setFlag(Integer flag) {
        this.flag = flag;
    }
    
    public String getDownDate() {
        return this.downDate;
    }
    
    public void setDownDate(String downDate) {
        this.downDate = downDate;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public String getPackNo() {
        return this.packNo;
    }
    
    public BigDecimal getPackVolume() {
        return this.packVolume;
    }
    
    public BigDecimal getPackWeight() {
        return this.packWeight;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setPackNo(String packNo) {
        this.packNo = packNo;
    }
    
    public void setPackVolume(BigDecimal packVolume) {
        this.packVolume = packVolume;
    }
    
    public void setPackWeight(BigDecimal packWeight) {
        this.packWeight = packWeight;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        
        PackInfo pack = (PackInfo) obj;
        if (this.orderId == null || this.packNo == null) {
            return this.orderId == pack.orderId || this.packNo == pack.packNo;
        } else {
            return this.orderId.equals(pack.orderId) && this.packNo.equals(pack.packNo);
        }
    }
    
    @Override
    public int hashCode() {
        return this.orderId.hashCode() + this.packNo.hashCode();
    }
}
