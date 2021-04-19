package com.jd.bluedragon.distribution.order.domain;


/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/15 14:07
 */
public class InternationOrderDto {
    /**
     * 订单号
     */
    private long id;
    /**
     * 机构名称
     */
    private String idCompanyBranchName;
    /**
     * 机构号
     */
    private int idCompanyBranch;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 配送中心
     */
    private int deliveryCenterID;
    /**
     *  仓ID
     */
    private int storeId;
    /**
     * 订单类型
     */
    private int orderType;
    private String sendPay;
    /**
     * 城市
     */
    private Integer city;

    /**
     * 省
     */
    private Integer province;


    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }



    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdCompanyBranchName() {
        return idCompanyBranchName;
    }

    public void setIdCompanyBranchName(String idCompanyBranchName) {
        this.idCompanyBranchName = idCompanyBranchName;
    }

    public int getIdCompanyBranch() {
        return idCompanyBranch;
    }

    public void setIdCompanyBranch(int idCompanyBranch) {
        this.idCompanyBranch = idCompanyBranch;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getDeliveryCenterID() {
        return deliveryCenterID;
    }

    public void setDeliveryCenterID(int deliveryCenterID) {
        this.deliveryCenterID = deliveryCenterID;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
}
    
