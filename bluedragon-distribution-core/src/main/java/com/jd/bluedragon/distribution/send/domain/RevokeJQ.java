package com.jd.bluedragon.distribution.send.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RevokeJQ")
public class RevokeJQ {

    /** 批次号 */
    private String batchId;
    
    /** 箱号 */
    private String boxId;
    
    /** 包裹号 */
    private String orderId;
    
    /** 取件单号 */
    private String queryOrderCode;
    
    /** 发货时间 */
    private String sendTime;
    
    /** 发货人 */
    private String operatorName;
    
    /** 发货单位 */
    private String destId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getQueryOrderCode() {
        return queryOrderCode;
    }

    public void setQueryOrderCode(String queryOrderCode) {
        this.queryOrderCode = queryOrderCode;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }
    
}
