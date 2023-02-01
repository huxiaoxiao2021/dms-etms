package com.jd.bluedragon.distribution.abnormalwaybill.domain;

import java.io.Serializable;

public class AbnormalWaybillDiff implements Serializable {
    //主键
    private Long id;
    //正确运单号
    private String waybillCodeC;
    //正确订单号
    private String orderIdC;
    //错误运单号(重复生成的运单号)
    private String waybillCodeE;
    //错误订单号(重复生成的运单号对应的订单号)
    private String orderIdE;
    //处理类型
    private String type;
    //有效标识 yn
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWaybillCodeC() {
        return waybillCodeC;
    }

    public void setWaybillCodeC(String waybillCodeC) {
        this.waybillCodeC = waybillCodeC;
    }

    public String getOrderIdC() {
        return orderIdC;
    }

    public void setOrderIdC(String orderIdC) {
        this.orderIdC = orderIdC;
    }

    public String getWaybillCodeE() {
        return waybillCodeE;
    }

    public void setWaybillCodeE(String waybillCodeE) {
        this.waybillCodeE = waybillCodeE;
    }

    public String getOrderIdE() {
        return orderIdE;
    }

    public void setOrderIdE(String orderIdE) {
        this.orderIdE = orderIdE;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
