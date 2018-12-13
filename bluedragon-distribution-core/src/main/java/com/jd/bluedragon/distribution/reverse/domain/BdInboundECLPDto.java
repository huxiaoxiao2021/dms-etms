package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author liuduo
 */
public class BdInboundECLPDto implements Serializable {
    private static final long serialVersionUID = 521655733620952638L;
    /**
     *销售出库单号
     */
    private String busiOrderCode;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 商家事业部ID
     */
    private String originDeptId;
    /**
     * 目的事业部编号
     */
    private String targetDeptNo;
    /**
     * 库房编号
     */
    private String storeId;
    /**
     * 配送中心编号
     */
    private String cky2;
    /**
     * 机构编号
     */
    private String orgId;
    /**
     * 是否可缺量
     */
    private Byte supportLack;
    /**
     * 理赔金额
     */
    private BigDecimal compensationMoney;
    /**
     * 操作人
     */
    private String operatorName;
    /**
     * 操作时间  yyyy-MM-dd hh:mm:ss
     */
    private String operateTime;
    /**
     * 商品明细
     */
    private List<BdInboundECLPDetail> goodsList;

    /**
     * 发货皮抄
     */
    private String sendCode;
    /**
     * 结算主体编码
     */
    private String settleSubjectCode;
    /**
     * 结算主体名称
     */
    private String settleSubjectName;

    public String getBusiOrderCode() {
        return busiOrderCode;
    }

    public void setBusiOrderCode(String busiOrderCode) {
        this.busiOrderCode = busiOrderCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOriginDeptId() {
        return originDeptId;
    }

    public void setOriginDeptId(String originDeptId) {
        this.originDeptId = originDeptId;
    }

    public String getTargetDeptNo() {
        return targetDeptNo;
    }

    public void setTargetDeptNo(String targetDeptNo) {
        this.targetDeptNo = targetDeptNo;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCky2() {
        return cky2;
    }

    public void setCky2(String cky2) {
        this.cky2 = cky2;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Byte getSupportLack() {
        return supportLack;
    }

    public void setSupportLack(Byte supportLack) {
        this.supportLack = supportLack;
    }

    public BigDecimal getCompensationMoney() {
        return compensationMoney;
    }

    public void setCompensationMoney(BigDecimal compensationMoney) {
        this.compensationMoney = compensationMoney;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public List<BdInboundECLPDetail> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<BdInboundECLPDetail> goodsList) {
        this.goodsList = goodsList;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getSettleSubjectCode() {
        return settleSubjectCode;
    }

    public void setSettleSubjectCode(String settleSubjectCode) {
        this.settleSubjectCode = settleSubjectCode;
    }

    public String getSettleSubjectName() {
        return settleSubjectName;
    }

    public void setSettleSubjectName(String settleSubjectName) {
        this.settleSubjectName = settleSubjectName;
    }

    @Override
    public String toString() {
        return "BdInboundECLPDto{" +
                "busiOrderCode='" + busiOrderCode + '\'' +
                ", waybillCode='" + waybillCode + '\'' +
                ", originDeptId='" + originDeptId + '\'' +
                ", targetDeptNo='" + targetDeptNo + '\'' +
                ", storeId='" + storeId + '\'' +
                ", cky2='" + cky2 + '\'' +
                ", orgId='" + orgId + '\'' +
                ", supportLack=" + supportLack +
                ", compensationMoney=" + compensationMoney +
                ", operatorName='" + operatorName + '\'' +
                ", operateTime=" + operateTime +
                ", goodsList=" + goodsList +
                '}';
    }
}
