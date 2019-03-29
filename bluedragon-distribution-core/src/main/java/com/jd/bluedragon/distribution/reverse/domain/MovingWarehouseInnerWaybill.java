package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
import java.util.List;

public class MovingWarehouseInnerWaybill implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 批次号
     */
    private String busiOrderCode;

    /**
     * 配送中心
     */
    private String deliveryCenterId;

    /**
     * 库房号
     */
    private String warehouseId;

    /**
     * 箱号集合（移动仓内配单号集合）
     */
    private List<String> caseNos;

    public String getBusiOrderCode() {
        return busiOrderCode;
    }

    public void setBusiOrderCode(String busiOrderCode) {
        this.busiOrderCode = busiOrderCode;
    }

    public String getDeliveryCenterId() {
        return deliveryCenterId;
    }

    public void setDeliveryCenterId(String deliveryCenterId) {
        this.deliveryCenterId = deliveryCenterId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public List<String> getCaseNos() {
        return caseNos;
    }

    public void setCaseNos(List<String> caseNos) {
        this.caseNos = caseNos;
    }
}
