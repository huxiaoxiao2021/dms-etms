package com.jd.bluedragon.distribution.send.domain.reverse;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DsOrder")
public class DsOrder {
    
    private int id = 0;
    private int orderType = 0;
    private int packSum = 0;
    private int queryOrderId = 0;
    private int sourceSiteId = 0;
    private int storeId = 0;
    private int flag = 0;
    private int sourceDCId = 0;
    private String batchId = "";
    private String boxRefId = "";
    private String deliveryType = "";
    private String orderId = "";
    private String remark = "";
    private String destId = "";
    private String queryOrderCode = "";
    private List<DsPack> packList = new ArrayList<DsPack>();
    
    public String getBatchId() {
        return this.batchId;
    }
    
    public String getBoxRefId() {
        return this.boxRefId;
    }
    
    public String getDeliveryType() {
        return this.deliveryType;
    }
    
    public String getDestId() {
        return this.destId;
    }
    
    public int getFlag() {
        return this.flag;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public int getOrderType() {
        return this.orderType;
    }
    
    public List<DsPack> getPackList() {
        return this.packList;
    }
    
    public int getPackSum() {
        return this.packSum;
    }
    
    public String getQueryOrderCode() {
        return this.queryOrderCode;
    }
    
    public int getQueryOrderId() {
        return this.queryOrderId;
    }
    
    public String getRemark() {
        return this.remark;
    }
    
    public int getSourceDCId() {
        return this.sourceDCId;
    }
    
    public int getSourceSiteId() {
        return this.sourceSiteId;
    }
    
    public int getStoreId() {
        return this.storeId;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    public void setBoxRefId(String boxRefId) {
        this.boxRefId = boxRefId;
    }
    
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }
    
    public void setDestId(String destId) {
        this.destId = destId;
    }
    
    public void setFlag(int flag) {
        this.flag = flag;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
    
    public void setPackList(List<DsPack> packList) {
        this.packList = packList;
    }
    
    public void setPackSum(int packSum) {
        this.packSum = packSum;
    }
    
    public void setQueryOrderCode(String queryOrderCode) {
        this.queryOrderCode = queryOrderCode;
    }
    
    public void setQueryOrderId(int queryOrderId) {
        this.queryOrderId = queryOrderId;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public void setSourceDCId(int sourceDCId) {
        this.sourceDCId = sourceDCId;
    }
    
    public void setSourceSiteId(int sourceSiteId) {
        this.sourceSiteId = sourceSiteId;
    }
    
    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        
        DsOrder order = (DsOrder) obj;
        
        
        if (this.batchId == null) {
        	return false;
        }
        if (this.boxRefId == null) {
        	return false;
        }
        if (this.orderId == null) {
        	return false;
        }
        return this.batchId.equals(order.batchId) && this.boxRefId.equals(order.boxRefId)
                && this.orderId.equals(order.orderId);
    }
    
    @Override
    public int hashCode() {
        return this.orderId.hashCode();
    }
}
