package com.jd.bluedragon.distribution.waybill.domain;

import com.jd.preseparate.vo.BaseRequest;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
public class LabelPrintingRequest  extends BaseRequest {

    /**
     *
     */
    private static final long serialVersionUID = 1502329487637273145L;

    /**订单号*/
    public String orderCode;

    /**运单号*/
    public String waybillCode;

    /**分拣中心id 分拣中心打印时调用*/
    public Integer dmsCode;

    /**分拣中心名称 分拣中心打印时调用*/
    public String dmsName;

    /**现场调度 1为现场调度 0为非现场调度*/
    public Integer localSchedule;

    /**航空标识 true 航空*/
    public boolean airTransport;

    /**预分拣站点编号*/
    public Integer preSeparateCode;

    /**预分拣站点名称*/
    public String preSeparateName;

    /**库房code 备件库打印时调用*/
    public Integer storeCode;

    /**库房名称 备件库打印时调用*/
    public String storeName;

    /**cky2*/
    public Integer cky2;

    /**机构编号*/
    public Integer orgCode;

    /**机构名称*/
    public String orgName;

    /**打印来源 1.分拣中心;2.备件库*/
    public String originalType;

    /**标签类型 0：有纸化，1：无纸化*/
    public Integer labelType;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getDmsCode() {
        return dmsCode;
    }

    public void setDmsCode(Integer dmsCode) {
        this.dmsCode = dmsCode;
    }

    public String getDmsName() {
        return dmsName;
    }

    public void setDmsName(String dmsName) {
        this.dmsName = dmsName;
    }

    public Integer getLocalSchedule() {
        return localSchedule;
    }

    public void setLocalSchedule(Integer localSchedule) {
        this.localSchedule = localSchedule;
    }

    public boolean isAirTransport() {
        return airTransport;
    }

    public void setAirTransport(boolean airTransport) {
        this.airTransport = airTransport;
    }

    public Integer getPreSeparateCode() {
        return preSeparateCode;
    }

    public void setPreSeparateCode(Integer preSeparateCode) {
        this.preSeparateCode = preSeparateCode;
    }

    public String getPreSeparateName() {
        return preSeparateName;
    }

    public void setPreSeparateName(String preSeparateName) {
        this.preSeparateName = preSeparateName;
    }

    public Integer getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(Integer storeCode) {
        this.storeCode = storeCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getCky2() {
        return cky2;
    }

    public void setCky2(Integer cky2) {
        this.cky2 = cky2;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOriginalType() {
        return originalType;
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }

    public Integer getLabelType() {
        return labelType;
    }

    public void setLabelType(Integer labelType) {
        this.labelType = labelType;
    }

    @Override
    public String toString() {
        return "LabelPrintingRequest [waybillCode=" + waybillCode
                + ", dmsCode=" + dmsCode + ", dmsName=" + dmsName
                + ", localSchedule=" + localSchedule + ", airTransport="
                + airTransport + ", preSeparateCode=" + preSeparateCode
                + ", preSeparateName=" + preSeparateName + ", storeCode="
                + storeCode + ", storeName=" + storeName + ", cky2=" + cky2
                + ", orgCode=" + orgCode + ", orgName=" + orgName
                + ", originalType=" + originalType + ", labelType=" + labelType
                + "]";
    }
}
