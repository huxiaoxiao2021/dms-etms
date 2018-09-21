package com.jd.bluedragon.distribution.rma.response;

import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;

import java.util.List;

/**
 * RMA交接打印实体
 * <p>
 * <p>
 * Created by lixin39 on 2018/9/21.
 */
public class RmaHandoverPrint {

    /**
     * 打印日期
     */
    private String printDate;

    /**
     * 发货城市名称
     */
    private String sendCityName;

    /**
     * 操作站点名称
     */
    private String createSiteName;

    /**
     * 目的城市名称 : 省 + 城市
     */
    private String targetCityName;

    /**
     * 发货操作人名称
     */
    private String sendUserName;

    /**
     * 发货操作人电话
     */
    private String sendUserMobile;

    /**
     * 商家名称
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
     * 运单数量
     */
    private Integer waybillCount;

    /**
     * 包裹数量
     */
    private Integer packageCount;

    /**
     * 备件数量
     */
    private Integer spareCount;

    /**
     * 交接明细列表明细
     */
    private List<RmaHandoverDetail> handoverDetails;

    public String getPrintDate() {
        return printDate;
    }

    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    public String getSendCityName() {
        return sendCityName;
    }

    public void setSendCityName(String sendCityName) {
        this.sendCityName = sendCityName;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getTargetCityName() {
        return targetCityName;
    }

    public void setTargetCityName(String targetCityName) {
        this.targetCityName = targetCityName;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public void setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
    }

    public String getSendUserMobile() {
        return sendUserMobile;
    }

    public void setSendUserMobile(String sendUserMobile) {
        this.sendUserMobile = sendUserMobile;
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

    public Integer getWaybillCount() {
        return waybillCount;
    }

    public void setWaybillCount(Integer waybillCount) {
        this.waybillCount = waybillCount;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getSpareCount() {
        return spareCount;
    }

    public void setSpareCount(Integer spareCount) {
        this.spareCount = spareCount;
    }

    public List<RmaHandoverDetail> getHandoverDetails() {
        return handoverDetails;
    }

    public void setHandoverDetails(List<RmaHandoverDetail> handoverDetails) {
        this.handoverDetails = handoverDetails;
    }
}
