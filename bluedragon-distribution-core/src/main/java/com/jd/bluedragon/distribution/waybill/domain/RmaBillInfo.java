package com.jd.bluedragon.distribution.waybill.domain;

import java.io.Serializable;
import java.util.Date;

public class RmaBillInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long sysno;
    /**
     * 出库编码
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String package_barcode;

    /**
     * 订单类型 1-RMA
     */
    private Long waybill_type;


    /**
     * 出库单号 -- getSkuSnListByOrderId - orderId
     */
    private Long outboundOrderCode;

    /**
     * 商品编码  -- getSkuSnListByOrderId - skuCode
     */
    private String skuCode;

    /**
     * 备件条码  -- 运单接口 -- Goods -- sku
     */
    private String spareCode;

    /**
     * 商品名称  -- 运单接口 -- Goods -- sku
     */
    private String goodName;

    /**
     * 商品数量
     */
    private Long goodCount;
    /**
     * 异常备注
     */
    private String execptionRemark;

    /**
     * 包裹数 -- 运单接口 -- waybill -- goodNumber
     */
    private Long packageNum;

    /**
     * 发货城市编号 ?
     */
    private Long sendCityId;

    /**
     * 发货城市名称  ?
     */
    private String sendCityName;

    /**
     * 操作站点编号
     */
    private String createSiteCode;

    /**
     * 操作站点名称
     */
    private String createSiteName;

    /**
     * 目的省编号省
     */
    private Long targetProvinceId;

    /**
     * 省
     */
    private String targetProvinceName;

    /**
     * 目的城市编号，一级 + 二级  ?
     */
    private Long targetCityId;

    /**
     * 目的城市，一级 + 二级  ?
     */
    private String targetCityName;

    /**
     * 发货操作人编号  -- 发货mq
     */
    private String sendOperatorCode;

    /**
     * 发货操作人 --发货mq
     */
    private String sendOperatorName;

    /**
     * 发货操作人erp  --发货mq
     */
    private String sendOperatorErp;

    /**
     * 分拣发货人电话 -- 暂时不取
     */
    private String sendOperatorTel;

    /**
     * 商家名称  -- 运单接口 -- waybill
     */
    private String busiName;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人电话
     */
    private String receiverMobile;

    /**
     * 收货人地址
     */
    private String receiverAddress;

    /**
     * 是否打印
     */
    private Long isPrint;


    /**
     * 打印操作人编号  写入es
     */
    private String printOperatorCode;

    /**
     * 打印日期   写入es
     */
    private Date printDate;

    /**
     * 发货时间
     */
    private Date sendDate;

    /**
     * 发货开始结束时间
     */
    public String startDate;
    public String endDate;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackage_barcode() {
        return package_barcode;
    }

    public void setPackage_barcode(String package_barcode) {
        this.package_barcode = package_barcode;
    }

    public Long getWaybill_type() {
        return waybill_type;
    }

    public void setWaybill_type(Long waybill_type) {
        this.waybill_type = waybill_type;
    }

    public Long getOutboundOrderCode() {
        return outboundOrderCode;
    }

    public void setOutboundOrderCode(Long outboundOrderCode) {
        this.outboundOrderCode = outboundOrderCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getSpareCode() {
        return spareCode;
    }

    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Long getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Long goodCount) {
        this.goodCount = goodCount;
    }

    public String getExecptionRemark() {
        return execptionRemark;
    }

    public void setExecptionRemark(String execptionRemark) {
        this.execptionRemark = execptionRemark;
    }

    public Long getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Long packageNum) {
        this.packageNum = packageNum;
    }

    public Long getSendCityId() {
        return sendCityId;
    }

    public void setSendCityId(Long sendCityId) {
        this.sendCityId = sendCityId;
    }

    public String getSendCityName() {
        return sendCityName;
    }

    public void setSendCityName(String sendCityName) {
        this.sendCityName = sendCityName;
    }

    public String getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(String createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Long getTargetProvinceId() {
        return targetProvinceId;
    }

    public void setTargetProvinceId(Long targetProvinceId) {
        this.targetProvinceId = targetProvinceId;
    }

    public String getTargetProvinceName() {
        return targetProvinceName;
    }

    public void setTargetProvinceName(String targetProvinceName) {
        this.targetProvinceName = targetProvinceName;
    }

    public Long getTargetCityId() {
        return targetCityId;
    }

    public void setTargetCityId(Long targetCityId) {
        this.targetCityId = targetCityId;
    }

    public String getTargetCityName() {
        return targetCityName;
    }

    public void setTargetCityName(String targetCityName) {
        this.targetCityName = targetCityName;
    }

    public String getSendOperatorCode() {
        return sendOperatorCode;
    }

    public void setSendOperatorCode(String sendOperatorCode) {
        this.sendOperatorCode = sendOperatorCode;
    }

    public String getSendOperatorName() {
        return sendOperatorName;
    }

    public void setSendOperatorName(String sendOperatorName) {
        this.sendOperatorName = sendOperatorName;
    }

    public String getSendOperatorErp() {
        return sendOperatorErp;
    }

    public void setSendOperatorErp(String sendOperatorErp) {
        this.sendOperatorErp = sendOperatorErp;
    }

    public String getSendOperatorTel() {
        return sendOperatorTel;
    }

    public void setSendOperatorTel(String sendOperatorTel) {
        this.sendOperatorTel = sendOperatorTel;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Long getIsPrint() {
        return isPrint;
    }

    public void setIsPrint(Long isPrint) {
        this.isPrint = isPrint;
    }

    public String getPrintOperatorCode() {
        return printOperatorCode;
    }

    public void setPrintOperatorCode(String printOperatorCode) {
        this.printOperatorCode = printOperatorCode;
    }

    public Date getPrintDate() {
        return printDate;
    }

    public void setPrintDate(Date printDate) {
        this.printDate = printDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
