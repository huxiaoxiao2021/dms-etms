package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2015/9/10.
 */
public class ExpressInfo implements Serializable {

    private static final long serialVersionUID = 6063221964438923597L;

    /**
     * 包裹号
     */
    private String packCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹重量
     */
    private Double packWeight;

    /**
     * 应收金额
     */
    private Double recMoney;

    /**
     * 机构
     */
    private Integer orgId;

    /**
     * 支付方式
     */
    private Integer payment;

    /**
     * 库房号
     */
    private Integer distributeStoreId;

    /**
     * 配送库房名称
     */
    private String distributeStoreName;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人地址
     */
    private String receiverAddress;

    /**
     * 收件人邮编
     */
    private String receiverPostcode;

    /**
     * 收件人城市
     */
    private String receiverCityname;

    /**
     * 收件人电话
     */
    private String receiverPhone;

    /**
     * 收件人手机
     */
    private String receiverMobile;

    /**
     * 配送方式
     */
    private Integer distributeType;

    /**
     * 代理学校地址
     */
    private String schoolAddress;

    /**
     * 代理学校邮编
     */
    private String schoolPostcode;

    /**
     * 代理学校收件人姓名
     */
    private String schoolReceiverName;

    /**
     * 代理学校收件人手机
     */
    private String schoolReceiverMobile;

    /**
     * 代理学校收件人电话
     */
    private String schoolReceiverPhone;

    private Integer siteId;

    private String siteName;

    public String getPackCode() {
        return packCode;
    }

    public void setPackCode(String packCode) {
        this.packCode = packCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Double getPackWeight() {
        return packWeight;
    }

    public void setPackWeight(Double packWeight) {
        this.packWeight = packWeight;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }


    public Integer getDistributeStoreId() {
        return distributeStoreId;
    }

    public void setDistributeStoreId(Integer distributeStoreId) {
        this.distributeStoreId = distributeStoreId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPostcode() {
        return receiverPostcode;
    }

    public void setReceiverPostcode(String receiverPostcode) {
        this.receiverPostcode = receiverPostcode;
    }

    public String getReceiverCityname() {
        return receiverCityname;
    }

    public void setReceiverCityname(String receiverCityname) {
        this.receiverCityname = receiverCityname;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public Integer getDistributeType() {
        return distributeType;
    }

    public void setDistributeType(Integer distributeType) {
        this.distributeType = distributeType;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolPostcode() {
        return schoolPostcode;
    }

    public void setSchoolPostcode(String schoolPostcode) {
        this.schoolPostcode = schoolPostcode;
    }

    public String getSchoolReceiverName() {
        return schoolReceiverName;
    }

    public void setSchoolReceiverName(String schoolReceiverName) {
        this.schoolReceiverName = schoolReceiverName;
    }

    public String getSchoolReceiverMobile() {
        return schoolReceiverMobile;
    }

    public void setSchoolReceiverMobile(String schoolReceiverMobile) {
        this.schoolReceiverMobile = schoolReceiverMobile;
    }

    public String getSchoolReceiverPhone() {
        return schoolReceiverPhone;
    }

    public void setSchoolReceiverPhone(String schoolReceiverPhone) {
        this.schoolReceiverPhone = schoolReceiverPhone;
    }

    public Double getRecMoney() {
        return recMoney;
    }

    public void setRecMoney(Double recMoney) {
        this.recMoney = recMoney;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDistributeStoreName() {
        return distributeStoreName;
    }

    public void setDistributeStoreName(String distributeStoreName) {
        this.distributeStoreName = distributeStoreName;
    }


}

