package com.jd.bluedragon.distribution.send.domain.reverse;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DsPack")
public class DsPack {
    
    private int id = 0;
    private int status = 0;
    private BigDecimal packWbulk = new BigDecimal("0.00");
    private BigDecimal packWeight = new BigDecimal("0.00");
    private String boxRefId = "";
    private String orderRefId = "";
    private String packId = "";
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        
        DsPack pack = (DsPack) obj;
        if (this.orderRefId == null || this.packId == null) {
            return this.orderRefId == pack.orderRefId || this.packId == pack.packId;
        }
        
        return this.orderRefId.equals(pack.orderRefId) && this.packId.equals(pack.packId);
    }
    
    public String getBoxRefId() {
        return this.boxRefId;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getOrderRefId() {
        return this.orderRefId;
    }
    
    public String getPackId() {
        return this.packId;
    }
    
    public BigDecimal getPackWbulk() {
        return this.packWbulk;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    @Override
    public int hashCode() {
        return this.orderRefId.hashCode() + this.packId.hashCode();
    }
    
    public void setBoxRefId(String boxRefId) {
        this.boxRefId = boxRefId;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setOrderRefId(String orderRefId) {
        this.orderRefId = orderRefId;
    }
    
    public void setPackId(String packId) {
        this.packId = packId;
    }
    
    public void setPackWbulk(BigDecimal packWbulk) {
        this.packWbulk = packWbulk;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
     * @return the packWeight
     */
    public BigDecimal getPackWeight() {
        return this.packWeight;
    }
    
    /**
     * @param packWeight
     *            the packWeight to set
     */
    public void setPackWeight(BigDecimal packWeight) {
        this.packWeight = packWeight;
    }
    
}
