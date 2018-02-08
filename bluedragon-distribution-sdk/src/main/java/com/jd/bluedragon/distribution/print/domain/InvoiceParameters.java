package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 发票请求参数
 * Created by wangtingwei on 2016/4/8.
 */
public class InvoiceParameters implements Serializable{
    private static final long serialVersionUID=1L;

    /**
     *订单编号
     */
    private long    orderId;

    /**
     *配送中心编号
     */
    private int     cky2;

    /**
     *库房编号
     */
    private int     storeId;

    /**
     *开票人erp账号
     */
    private String  erpAccount;

    /**
     * 开票人真实姓名
     */
    private String  realName;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getCky2() {
        return cky2;
    }

    public void setCky2(int cky2) {
        this.cky2 = cky2;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getErpAccount() {
        return erpAccount;
    }

    public void setErpAccount(String erpAccount) {
        this.erpAccount = erpAccount;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
