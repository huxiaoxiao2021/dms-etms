package com.jd.bluedragon.distribution.api.request;

import java.math.BigDecimal;

import com.jd.bluedragon.distribution.api.JdObject;

public class WmsOrderPackageRequest extends JdObject {
	
	private static final long serialVersionUID = -3948765914314667390L;
	
	public static final String UNIT_VOLUME = "CCM";
	public static final String UNIT_WEIGHT = "KG";
	
	/** 订单来源 WMS下传默认为'0' */
	private Integer orderType;
	
	/** 包裹数量 */
	private Integer packNum;
	
	/** ? */
	private Integer priority;
	
	/** 订单类型 */
	private Integer sendType;
	
	/** 是否调度在投 */
	private Integer reType;
	
	/** 默认为'0' */
	private Integer flag;
	
	/** 机构 */
	private String cky2;
	
	/** 库房编号 */
	private String storeId;
	
	/** 订单编号 */
	private String expNo;
	
	/** 站点编号 */
	private String custNo;
	
	/** 中转站编号 */
    private String transferStationId;
	
	/** 包裹号码 */
	private String packageSn;
	
	/** 打包时间 */
	private String packDate;
	
	/** 下传时间 */
	private String downDate;
	
	/** 打包员 */
	private String packWkNo;
	
	/** 客户地址 */
	private String address;
	
	/** 分拣中心标识 */
	private String dmsNo;
	
	/** 订单类型 */
	private String expType;
	
	/** 客户名称 */
	private String clientName;
	
	/** 客户手机 */
	private String clientTel;
	
	/** 客户手机 */
	private String phone;
	
	/** 重量单位 */
	private String weightUnit;
	
	/** 体积单位 */
	private String volumeUnit;
	
	/** 包裹打包重量 */
	private BigDecimal packWeight;
	
	/** 支付方式 */
	private Integer paymentType;
	
	/** 订单属性 */
	private String sendPay;
	
	/** 价格 */
	private BigDecimal price = BigDecimal.valueOf(0);
	
	/** 应付金额 */
	private BigDecimal shouldPay = BigDecimal.valueOf(0);
	
	/** 订单重量 */
	private BigDecimal weight = BigDecimal.valueOf(0);
	
	/** 订单体积 */
	private BigDecimal volume = BigDecimal.valueOf(0);
	
	public String getCky2() {
		return cky2;
	}
	
	public void setCky2(String cky2) {
		this.cky2 = cky2;
	}
	
	public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getExpNo() {
		return expNo;
	}
	
	public void setExpNo(String expNo) {
		this.expNo = expNo;
	}
	
	public String getCustNo() {
		return custNo;
	}
	
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	
	public Integer getOrderType() {
		return orderType;
	}
	
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	
	public Integer getPackNum() {
		return packNum;
	}
	
	public void setPackNum(Integer packNum) {
		this.packNum = packNum;
	}
	
	public String getPackageSn() {
		return packageSn;
	}
	
	public void setPackageSn(String packageSn) {
		this.packageSn = packageSn;
	}
	
	public BigDecimal getPackWeight() {
		return packWeight;
	}
	
	public void setPackWeight(BigDecimal packWeight) {
		this.packWeight = packWeight;
	}
	
	public Integer getPriority() {
		return priority;
	}
	
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Integer getSendType() {
		return sendType;
	}
	
	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}
	
	public String getPackDate() {
		return packDate;
	}
	
	public void setPackDate(String packDate) {
		this.packDate = packDate;
	}
	
	public String getDownDate() {
		return downDate;
	}
	
	public void setDownDate(String downDate) {
		this.downDate = downDate;
	}
	
	public String getPackWkNo() {
		return packWkNo;
	}
	
	public void setPackWkNo(String packWkNo) {
		this.packWkNo = packWkNo;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDmsNo() {
		return dmsNo;
	}
	
	public void setDmsNo(String dmsNo) {
		this.dmsNo = dmsNo;
	}
	
	public Integer getReType() {
		return reType;
	}
	
	public void setReType(Integer reType) {
		this.reType = reType;
	}
	
	public String getExpType() {
		return expType;
	}
	
	public void setExpType(String expType) {
		this.expType = expType;
	}
	
	public Integer getFlag() {
		return flag;
	}
	
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public BigDecimal getShouldPay() {
		return shouldPay;
	}
	
	public void setShouldPay(BigDecimal shouldPay) {
		this.shouldPay = shouldPay;
	}
	
	public Integer getPaymentType() {
		return paymentType;
	}
	
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public String getClientTel() {
		return clientTel;
	}
	
	public void setClientTel(String clientTel) {
		this.clientTel = clientTel;
	}
	
	public BigDecimal getWeight() {
		return weight;
	}
	
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	
	public BigDecimal getVolume() {
		return volume;
	}
	
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	
	public String getWeightUnit() {
		return weightUnit;
	}
	
	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}
	
	public String getVolumeUnit() {
		return volumeUnit;
	}
	
	public void setVolumeUnit(String volumeUnit) {
		this.volumeUnit = volumeUnit;
	}
	
	public String getSendPay() {
		return sendPay;
	}
	
	public void setSendPay(String sendPay) {
		this.sendPay = sendPay;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}

    public String getTransferStationId() {
        return transferStationId;
    }

    public void setTransferStationId(String transferStationId) {
        this.transferStationId = transferStationId;
    }
	
}
