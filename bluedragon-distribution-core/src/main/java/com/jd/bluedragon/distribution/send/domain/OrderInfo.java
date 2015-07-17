package com.jd.bluedragon.distribution.send.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单信息 1、用在Dms给tms发货时用作WebService输入参数中的订单信息 User: YinWei Date: 2011-4-20 Time:
 * 14:05:15
 */
public class OrderInfo {
    
    /** 箱子ID */
    private String boxId;
    
    /** 订单ID */
    private String orderId;
    
    /** 订单类型 */
    private String orderType;
    
    /** 订单来源 */
    private String orderSource;
    
    /** 公共时间 */
    private String time;
    
    /** 包裹数 */
    private int packNum;
    
    /** 操作人ID */
    private String operatorId;
    
    /** 操作人ERP账号 */
    private String operatorCode;
    
    /** 操作人姓名 */
    private String operatorName;
    
    /**
     * 订单原始站点（预分拣之后的站点）
     */
    private String zdId;
    
    private String createTime;
    
    private String remark;
    
    private String orderAdd;
    
    private String batchId;
    
    private String dispatchType;
    
    private int id;
    
    private String seqno;
    
    private String orgId;
    
    private String wareNo;
    
    private String expNo;
    
    private String custNo;
    
    private int sendType;
    
    private List<PackInfo> frPackInfos = new ArrayList<PackInfo>();
    
    /** 包裹信息 */
    private List<PackInfo> packInfoList = new ArrayList<PackInfo>();
    
    public String getZdId() {
        return this.zdId;
    }
    
    public void setZdId(String zdId) {
        this.zdId = zdId;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSeqno() {
        return this.seqno;
    }
    
    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }
    
    public String getOrgId() {
        return this.orgId;
    }
    
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    
    public String getWareNo() {
        return this.wareNo;
    }
    
    public void setWareNo(String wareNo) {
        this.wareNo = wareNo;
    }
    
    public String getExpNo() {
        return this.expNo;
    }
    
    public void setExpNo(String expNo) {
        this.expNo = expNo;
    }
    
    public String getCustNo() {
        return this.custNo;
    }
    
    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }
    
    public int getSendType() {
        return this.sendType;
    }
    
    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
    
    public List<PackInfo> getFrPackInfos() {
        return this.frPackInfos;
    }
    
    public void setFrPackInfos(List<PackInfo> frPackInfos) {
        this.frPackInfos = frPackInfos;
    }
    
    public String getOperatorId() {
        return this.operatorId;
    }
    
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getOperatorCode() {
        return this.operatorCode;
    }
    
    public String getOrderAdd() {
        return this.orderAdd;
    }
    
    public void setOrderAdd(String orderAdd) {
        this.orderAdd = orderAdd;
    }
    
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    
    public String getOperatorName() {
        return this.operatorName;
    }
    
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getRemark() {
        return this.remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getBoxId() {
        return this.boxId;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public String getOrderSource() {
        return this.orderSource;
    }
    
    public String getOrderType() {
        return this.orderType;
    }
    
    public List<PackInfo> getPackInfoList() {
        return this.packInfoList;
    }
    
    public int getPackNum() {
        return this.packNum;
    }
    
    public String getTime() {
        return this.time;
    }
    
    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public void setPackInfoList(List<PackInfo> packInfoList) {
        this.packInfoList = packInfoList;
    }
    
    public void setPackNum(int packNum) {
        this.packNum = packNum;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getBatchId() {
        return this.batchId;
    }
    
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
    
    public String getDispatchType() {
        return this.dispatchType;
    }
    
    public void setDispatchType(String dispatchType) {
        this.dispatchType = dispatchType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        
        OrderInfo order = (OrderInfo) obj;
        
        if (this.batchId == null) {
            return false;
        }
        if (this.boxId == null) {
        	 return false;
        }
        if (this.orderId == null) {
        	 return false;
        }
        
        return this.batchId.equals(order.batchId) && this.boxId.equals(order.boxId)
                && this.orderId.equals(order.orderId);
    }
    
    @Override
    public int hashCode() {
        return this.orderId.hashCode();
    }
    
}
