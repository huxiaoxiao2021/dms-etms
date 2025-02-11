package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * C网转B网通知调度系统MQ消息体
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月04日 16时:55分
 */
public class SendDispatchDto implements Serializable {

    private static final long serialVersionUID = 2292780849533744283L;

    /** 发货交接单号 */
    private String sendCode;

    /** 箱号 */
    private String boxCode;

    /** 包裹号 */
    private String packageBarcode;

    /** 运单号 */
    private String waybillCode;

    /** 操作单位编码 */
    private Integer createSiteCode;

    /** 操作单位编码 */
    private Integer receiveSiteCode;

    /** 目的分拣中心名称 */
    private String receiveSiteName;

    /** 操作时间 */
    private Date operateTime;

    /** 操作人 */
    private String createUser;

    /** 操作人编码 */
    private Integer createUserCode;

    /** 默认：DMS */
    private String source;

    /** 组板板号 */
    private String boardCode;

    /** waybillSign */
    private String waybillSign;

    /** 商家ID */
    private Integer busiId;

    /** 目的省份ID */
    private Integer endProvinceId;

    /** 目的城市ID */
    private Integer endCityId;

    /** 收件人详细地址 */
    private String endAddress;

    /** 收件人姓名 */
    private String receiverName;

    /** 收件人电话 */
    private String  receiverPhone;

    /** 支付类型  */
    private Integer paymentType;

    /** 下单时间 */
    private Date orderTime;

    /** 运单服务标示 */
    private String  sendPay;

    /** （目的地）车队7位编码 */
    private String  receiveDmsSiteCode;

    /** 预分拣站点Code  */
    private Integer preSiteCode;

    /** 预分拣站点名称 */
    private String  preSiteName;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public Integer getEndProvinceId() {
        return endProvinceId;
    }

    public void setEndProvinceId(Integer endProvinceId) {
        this.endProvinceId = endProvinceId;
    }

    public Integer getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Integer endCityId) {
        this.endCityId = endCityId;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getBusiId() {
        return busiId;
    }

    public void setBusiId(Integer busiId) {
        this.busiId = busiId;
    }

    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

    public String getReceiveDmsSiteCode() {
        return receiveDmsSiteCode;
    }

    public void setReceiveDmsSiteCode(String receiveDmsSiteCode) {
        this.receiveDmsSiteCode = receiveDmsSiteCode;
    }

    public Integer getPreSiteCode() {
        return preSiteCode;
    }

    public void setPreSiteCode(Integer preSiteCode) {
        this.preSiteCode = preSiteCode;
    }

    public String getPreSiteName() {
        return preSiteName;
    }

    public void setPreSiteName(String preSiteName) {
        this.preSiteName = preSiteName;
    }
}
