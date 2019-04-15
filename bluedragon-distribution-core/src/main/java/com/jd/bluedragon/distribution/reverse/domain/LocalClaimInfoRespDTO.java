package com.jd.bluedragon.distribution.reverse.domain;

import java.math.BigDecimal;

/**
 * 理赔数据
 */
public class LocalClaimInfoRespDTO{

    //物权归属 京东
    public static final Integer GOOD_OWNER_JD = 2;
    //物权归属 商家
    public static final Integer GOOD_OWNER_BUSI = 1;

    public static final String LP_STATUS_NONE = "无理赔";
    public static final String LP_STATUS_DOING = "理赔中";
    public static final String LP_STATUS_DONE = "理赔完成";

    //理赔金额
    private BigDecimal paymentRealMoney;
    //物权归属 1-商家，2-京东
    private int goodOwner;
    private String goodOwnerName;
    //理赔状态描述  理赔完成 理赔中 无理赔
    private String statusDesc;
    //结算主体名称
    private String settleSubjectName;
    //结算主体编码
    private String settleSubjectCode;
    //目的事业部编码
    private String divisionNumber;

    public String getDivisionNumber() {
        return divisionNumber;
    }

    public void setDivisionNumber(String divisionNumber) {
        this.divisionNumber = divisionNumber;
    }

    public BigDecimal getPaymentRealMoney() {
        return paymentRealMoney;
    }

    public void setPaymentRealMoney(BigDecimal paymentRealMoney) {
        this.paymentRealMoney = paymentRealMoney;
    }

    public int getGoodOwner() {
        return goodOwner;
    }

    public void setGoodOwner(int goodOwner) {
        this.goodOwner = goodOwner;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getSettleSubjectName() {
        return settleSubjectName;
    }

    public void setSettleSubjectName(String settleSubjectName) {
        this.settleSubjectName = settleSubjectName;
    }

    public String getSettleSubjectCode() {
        return settleSubjectCode;
    }

    public void setSettleSubjectCode(String settleSubjectCode) {
        this.settleSubjectCode = settleSubjectCode;
    }

    public String getGoodOwnerName() {
        return goodOwnerName;
    }

    public void setGoodOwnerName(String goodOwnerName) {
        this.goodOwnerName = goodOwnerName;
    }
}
