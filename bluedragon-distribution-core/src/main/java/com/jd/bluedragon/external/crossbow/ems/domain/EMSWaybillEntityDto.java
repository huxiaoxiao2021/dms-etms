package com.jd.bluedragon.external.crossbow.ems.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 *     推送给全国邮政的运单面单信息javaBean
 *     无历史文档，字段信息已经丢失
 *
 * @author wuzuxiang
 * @since 2019/12/27
 **/
@XmlRootElement(name = "printData")
@XmlAccessorType(XmlAccessType.FIELD)
public class EMSWaybillEntityDto {

    @XmlElement(name = "businessType")
    private String businessType;

    @XmlElement(name = "dateType")
    private String dateType;

    @XmlElement(name = "procdate")
    private String procdate;

    @XmlElement(name = "scontactor")
    private String scontactor;

    @XmlElement(name = "scustMobile")
    private String scustMobile;

    @XmlElement(name = "scustTelplus")
    private String scustTelplus;

    @XmlElement(name = "scustPost")
    private String scustPost;

    @XmlElement(name = "scustAddr")
    private String scustAddr;

    @XmlElement(name = "scustComp")
    private String scustComp;

    @XmlElement(name = "tcontactor")
    private String tcontactor;

    @XmlElement(name = "tcustMobile")
    private String tcustMobile;

    @XmlElement(name = "tcustTelplus")
    private String tcustTelplus;

    @XmlElement(name = "tcustPost")
    private String tcustPost;

    @XmlElement(name = "tcustAddr")
    private String tcustAddr;

    @XmlElement(name = "tcustComp")
    private String tcustComp;

    @XmlElement(name = "tcustProvince")
    private String tcustProvince;

    @XmlElement(name = "tcustCity")
    private String tcustCity;

    @XmlElement(name = "tcustCounty")
    private String tcustCounty;

    @XmlElement(name = "weight")
    private String weight;

    @XmlElement(name = "length")
    private String length;

    @XmlElement(name = "insure")
    private String insure;

    @XmlElement(name = "fee")
    private String fee;

    @XmlElement(name = "feeUppercase")
    private String feeUppercase;

    @XmlElement(name = "cargoDesc")
    private String cargoDesc;

    @XmlElement(name = "cargoDesc1")
    private String cargoDesc1;

    @XmlElement(name = "cargoDesc2")
    private String cargoDesc2;

    @XmlElement(name = "cargoDesc3")
    private String cargoDesc3;

    @XmlElement(name = "cargoDesc4")
    private String cargoDesc4;

    @XmlElement(name = "cargoType")
    private String cargoType;

    @XmlElement(name = "deliveryclaim")
    private String deliveryclaim;

    @XmlElement(name = "remark")
    private String remark;

    @XmlElement(name = "bigAccountDataId")
    private String bigAccountDataId;

    @XmlElement(name = "customerDn")
    private String customerDn;

    @XmlElement(name = "subBillCount")
    private String subBillCount;

    @XmlElement(name = "mainBillNo")
    private String mainBillNo;

    @XmlElement(name = "mainBillFlag")
    private String mainBillFlag;

    @XmlElement(name = "mainSubPayMode")
    private String mainSubPayMode;

    @XmlElement(name = "payMode")
    private String payMode;

    @XmlElement(name = "insureType")
    private String insureType;

    @XmlElement(name = "blank1")
    private String blank1;

    @XmlElement(name = "blank2")
    private String blank2;

    @XmlElement(name = "blank3")
    private String blank3;

    @XmlElement(name = "blank4")
    private String blank4;

    @XmlElement(name = "blank5")
    private String blank5;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getProcdate() {
        return procdate;
    }

    public void setProcdate(String procdate) {
        this.procdate = procdate;
    }

    public String getScontactor() {
        return scontactor;
    }

    public void setScontactor(String scontactor) {
        this.scontactor = scontactor;
    }

    public String getScustMobile() {
        return scustMobile;
    }

    public void setScustMobile(String scustMobile) {
        this.scustMobile = scustMobile;
    }

    public String getScustTelplus() {
        return scustTelplus;
    }

    public void setScustTelplus(String scustTelplus) {
        this.scustTelplus = scustTelplus;
    }

    public String getScustPost() {
        return scustPost;
    }

    public void setScustPost(String scustPost) {
        this.scustPost = scustPost;
    }

    public String getScustAddr() {
        return scustAddr;
    }

    public void setScustAddr(String scustAddr) {
        this.scustAddr = scustAddr;
    }

    public String getScustComp() {
        return scustComp;
    }

    public void setScustComp(String scustComp) {
        this.scustComp = scustComp;
    }

    public String getTcontactor() {
        return tcontactor;
    }

    public void setTcontactor(String tcontactor) {
        this.tcontactor = tcontactor;
    }

    public String getTcustMobile() {
        return tcustMobile;
    }

    public void setTcustMobile(String tcustMobile) {
        this.tcustMobile = tcustMobile;
    }

    public String getTcustTelplus() {
        return tcustTelplus;
    }

    public void setTcustTelplus(String tcustTelplus) {
        this.tcustTelplus = tcustTelplus;
    }

    public String getTcustPost() {
        return tcustPost;
    }

    public void setTcustPost(String tcustPost) {
        this.tcustPost = tcustPost;
    }

    public String getTcustAddr() {
        return tcustAddr;
    }

    public void setTcustAddr(String tcustAddr) {
        this.tcustAddr = tcustAddr;
    }

    public String getTcustComp() {
        return tcustComp;
    }

    public void setTcustComp(String tcustComp) {
        this.tcustComp = tcustComp;
    }

    public String getTcustProvince() {
        return tcustProvince;
    }

    public void setTcustProvince(String tcustProvince) {
        this.tcustProvince = tcustProvince;
    }

    public String getTcustCity() {
        return tcustCity;
    }

    public void setTcustCity(String tcustCity) {
        this.tcustCity = tcustCity;
    }

    public String getTcustCounty() {
        return tcustCounty;
    }

    public void setTcustCounty(String tcustCounty) {
        this.tcustCounty = tcustCounty;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getInsure() {
        return insure;
    }

    public void setInsure(String insure) {
        this.insure = insure;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFeeUppercase() {
        return feeUppercase;
    }

    public void setFeeUppercase(String feeUppercase) {
        this.feeUppercase = feeUppercase;
    }

    public String getCargoDesc() {
        return cargoDesc;
    }

    public void setCargoDesc(String cargoDesc) {
        this.cargoDesc = cargoDesc;
    }

    public String getCargoDesc1() {
        return cargoDesc1;
    }

    public void setCargoDesc1(String cargoDesc1) {
        this.cargoDesc1 = cargoDesc1;
    }

    public String getCargoDesc2() {
        return cargoDesc2;
    }

    public void setCargoDesc2(String cargoDesc2) {
        this.cargoDesc2 = cargoDesc2;
    }

    public String getCargoDesc3() {
        return cargoDesc3;
    }

    public void setCargoDesc3(String cargoDesc3) {
        this.cargoDesc3 = cargoDesc3;
    }

    public String getCargoDesc4() {
        return cargoDesc4;
    }

    public void setCargoDesc4(String cargoDesc4) {
        this.cargoDesc4 = cargoDesc4;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public String getDeliveryclaim() {
        return deliveryclaim;
    }

    public void setDeliveryclaim(String deliveryclaim) {
        this.deliveryclaim = deliveryclaim;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBigAccountDataId() {
        return bigAccountDataId;
    }

    public void setBigAccountDataId(String bigAccountDataId) {
        this.bigAccountDataId = bigAccountDataId;
    }

    public String getCustomerDn() {
        return customerDn;
    }

    public void setCustomerDn(String customerDn) {
        this.customerDn = customerDn;
    }

    public String getSubBillCount() {
        return subBillCount;
    }

    public void setSubBillCount(String subBillCount) {
        this.subBillCount = subBillCount;
    }

    public String getMainBillNo() {
        return mainBillNo;
    }

    public void setMainBillNo(String mainBillNo) {
        this.mainBillNo = mainBillNo;
    }

    public String getMainBillFlag() {
        return mainBillFlag;
    }

    public void setMainBillFlag(String mainBillFlag) {
        this.mainBillFlag = mainBillFlag;
    }

    public String getMainSubPayMode() {
        return mainSubPayMode;
    }

    public void setMainSubPayMode(String mainSubPayMode) {
        this.mainSubPayMode = mainSubPayMode;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getInsureType() {
        return insureType;
    }

    public void setInsureType(String insureType) {
        this.insureType = insureType;
    }

    public String getBlank1() {
        return blank1;
    }

    public void setBlank1(String blank1) {
        this.blank1 = blank1;
    }

    public String getBlank2() {
        return blank2;
    }

    public void setBlank2(String blank2) {
        this.blank2 = blank2;
    }

    public String getBlank3() {
        return blank3;
    }

    public void setBlank3(String blank3) {
        this.blank3 = blank3;
    }

    public String getBlank4() {
        return blank4;
    }

    public void setBlank4(String blank4) {
        this.blank4 = blank4;
    }

    public String getBlank5() {
        return blank5;
    }

    public void setBlank5(String blank5) {
        this.blank5 = blank5;
    }
}
