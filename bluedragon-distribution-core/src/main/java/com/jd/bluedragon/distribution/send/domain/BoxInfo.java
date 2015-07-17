package com.jd.bluedragon.distribution.send.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 箱子信息 1、用在Dms给tms发货时用作WebService输入参数
 */
public class BoxInfo {
    
    /** 批次 */
    private String batchId;
    
    /** 箱号 */
    private String boxId;
    
    private String sendType;
    
    /** 发货ID，和类型匹配 */
    private String sendId;
    
    private String sendId1;
    
    /** 发货名称，和发货ID匹配 */
    private String sendName;
    
    /** 司机Id（承运人） */
    private String carrierId;
    
    /** 司机姓名（承运人） */
    private String carrierName;
    
    /** 操作人ID */
    private String operatorId;
    
    /** 操作人ERP账号 */
    private String operatorCode;
    
    /** 操作人姓名 */
    private String operatorName;
    
    /** 订单信息 */
    private List<OrderInfo> orderInfoList = new ArrayList<OrderInfo>();
    
    public String getBatchId() {
        return this.batchId;
    }
    
    public String getBoxId() {
        return this.boxId;
    }
    
    public String getCarrierId() {
        return this.carrierId;
    }
    
    public String getCarrierName() {
        return this.carrierName;
    }
    
    public String getOperatorCode() {
        return this.operatorCode;
    }
    
    public String getOperatorId() {
        return this.operatorId;
    }
    
    public String getOperatorName() {
        return this.operatorName;
    }
    
    public List<OrderInfo> getOrderInfoList() {
        return this.orderInfoList;
    }
    
    public String getSendId() {
        return this.sendId;
    }
    
    public String getSendName() {
        return this.sendName;
    }
    
    public String getSendType() {
        return this.sendType;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }
    
    public void setCarrierId(String carrierId) {
        this.carrierId = carrierId;
    }
    
    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }
    
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
    public void setOrderInfoList(List<OrderInfo> orderInfoList) {
        this.orderInfoList = orderInfoList;
    }
    
    public void setSendId(String sendId) {
        this.sendId = sendId;
    }
    
    public void setSendName(String sendName) {
        this.sendName = sendName;
    }
    
    public void setSendType(String sendType) {
        this.sendType = sendType;
    }
    
    public String getSendId1() {
        return this.sendId1;
    }
    
    public void setSendId1(String sendId1) {
        this.sendId1 = sendId1;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        
        BoxInfo box = (BoxInfo) obj;
        if (this.batchId == null || this.boxId == null) {
            return this.batchId == box.batchId || this.boxId == box.boxId;
        } else {
            return this.batchId.equals(box.batchId) && this.boxId.equals(box.boxId);
        }
    }
    
    @Override
    public int hashCode() {
        return this.batchId.hashCode() + this.boxId.hashCode();
    }
}
