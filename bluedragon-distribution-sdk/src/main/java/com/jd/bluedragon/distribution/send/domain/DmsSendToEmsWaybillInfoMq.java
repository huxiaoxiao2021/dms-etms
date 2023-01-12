package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 分拣发邮政消息mq
 * @author wuyoude
 */
public class DmsSendToEmsWaybillInfoMq implements Serializable {

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
    /**
     * 操作人id
     */
    private Integer operatorId;
    /**
     * 操作人name
     */
    private String operatorName;
	/**
	 * 寄件人
	 */
	private String consigner;
	/**
	 * 寄件人客户ID
	 */
	private Integer consignerId;
	/**
	 * 寄件人手机号
	 */
	private String consignerMobile;
	/**
	 * 寄件人座机号
	 */
	private String consignerTel;
	/**
	 * 寄件人地址
	 */
	private String consignerAddress;
	/**
	 * 寄件人-省
	 */
    private Integer consignerProvinceId;
	/**
	 * 寄件人-市
	 */
    private Integer consignerCityId;
	/**
	 * 寄件人-县
	 */
    private Integer consignerCountryId;
	/**
	 * 寄件人-镇
	 */
    private Integer consignerTownId;
	/**
	 * 收件人
	 */
	private String receiverName;
	/**
	 * 收件人手机号
	 */
	private String receiverMobile;
	/**
	 * 收件人座机号
	 */
	private String receiverTel;
	/**
	 * 收件人地址
	 */
	private String receiverAddress;
	/**
	 * 收件人-省
	 */
    private Integer receiverProvinceId;
	/**
	 * 收件人-市
	 */
    private Integer receiverCityId;
	/**
	 * 收件人-县
	 */
    private Integer receiverCountryId;
	/**
	 * 收件人-镇
	 */
    private Integer receiverTownId;
    
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
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
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
	public Integer getBusiId() {
		return busiId;
	}
	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
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
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getConsigner() {
		return consigner;
	}
	public void setConsigner(String consigner) {
		this.consigner = consigner;
	}
	public Integer getConsignerId() {
		return consignerId;
	}
	public void setConsignerId(Integer consignerId) {
		this.consignerId = consignerId;
	}
	public String getConsignerMobile() {
		return consignerMobile;
	}
	public void setConsignerMobile(String consignerMobile) {
		this.consignerMobile = consignerMobile;
	}
	public String getConsignerTel() {
		return consignerTel;
	}
	public void setConsignerTel(String consignerTel) {
		this.consignerTel = consignerTel;
	}
	public String getConsignerAddress() {
		return consignerAddress;
	}
	public void setConsignerAddress(String consignerAddress) {
		this.consignerAddress = consignerAddress;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverMobile() {
		return receiverMobile;
	}
	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}
	public String getReceiverTel() {
		return receiverTel;
	}
	public void setReceiverTel(String receiverTel) {
		this.receiverTel = receiverTel;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public Integer getReceiverProvinceId() {
		return receiverProvinceId;
	}
	public void setReceiverProvinceId(Integer receiverProvinceId) {
		this.receiverProvinceId = receiverProvinceId;
	}
	public Integer getReceiverCityId() {
		return receiverCityId;
	}
	public void setReceiverCityId(Integer receiverCityId) {
		this.receiverCityId = receiverCityId;
	}
	public Integer getReceiverCountryId() {
		return receiverCountryId;
	}
	public void setReceiverCountryId(Integer receiverCountryId) {
		this.receiverCountryId = receiverCountryId;
	}
	public Integer getReceiverTownId() {
		return receiverTownId;
	}
	public void setReceiverTownId(Integer receiverTownId) {
		this.receiverTownId = receiverTownId;
	}
	public Integer getConsignerProvinceId() {
		return consignerProvinceId;
	}
	public void setConsignerProvinceId(Integer consignerProvinceId) {
		this.consignerProvinceId = consignerProvinceId;
	}
	public Integer getConsignerCityId() {
		return consignerCityId;
	}
	public void setConsignerCityId(Integer consignerCityId) {
		this.consignerCityId = consignerCityId;
	}
	public Integer getConsignerCountryId() {
		return consignerCountryId;
	}
	public void setConsignerCountryId(Integer consignerCountryId) {
		this.consignerCountryId = consignerCountryId;
	}
	public Integer getConsignerTownId() {
		return consignerTownId;
	}
	public void setConsignerTownId(Integer consignerTownId) {
		this.consignerTownId = consignerTownId;
	}
}
