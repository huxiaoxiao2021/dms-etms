package com.jd.bluedragon.external.crossbow.postal.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *     运单信息传输接口请求对象
 *
 * @author wuyoude
 **/
public class WaybillInfoRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String doType;                // 操作类型	A新增 U修改 D删除                        
	private String brandCode;             // 接入平台的标识	由快递协同信息平台提供             
	private String waybillNo;             // 运单号	                                           
	private String productCode;           // 产品类型	1标准类产品，2经济类产品                 
	private String isInternational;       // 国际标识	4国内，5国际                             
	private String pickupOrgName;         // 收寄机构名称	                                     
	private String pickupOrgCode;         // 收寄机构编码	                                     
	private Long pickupTime;            // 收寄时间	10位时间戳                               
	private String pickupAttribute;       // 收寄公司代码	                                     
	private String senderProvinceNo;      // 收寄省份名称	收寄机构所在省份                     
	private String realWeight;            // 实际重量	单位g,和计泡重量二选一                   
	private String volWeight;             // 计泡重量	单位g,和实际重量二选一                   
	private String remarks;               // 邮件备注	                                         
	private String innerPropertyCode;     // 内件性质	1：文件 3：物品                          
	private String customerNo;            // 大客户编码	邮政与快递公司结算备用                 
	private String senderLinker;          // 寄件人姓名	                                       
	private String senderMobile;          // 寄件人手机	                                       
	private String senderAddress;         // 寄件人地址	含省市县（区）的全地址                 
	private String receiverLinker;        // 收件人姓名	                                       
	private String receiverAddress;       // 收件人地址	含省市县（区）的全地址                 
	private String receiverMobile;        // 收件人手机	                                       
	private Double  length;                // 长	厘米                                           
	private Double  width;                 // 宽	厘米                                           
	private Double  height;                // 高	厘米                                           
	private String codFlag;               // 代收款标志	1:代收货款2:代缴费9:无                 
	private String codAmount;             // 代收款金额	元                                     
	private String receiptFlag;           // 回单标志	5:电子返单7:实物返单                     
	private String receiptWaybillNo;      // 回单运单号	                                       
	private String insuranceFlag;         // 保价标识	1保价，2非保价                           
	private BigDecimal premiumAmount;     // 保价（保险）金额	元         
	private String senderRegionCode;      // 寄件地址编码	12位国家标准行政区划编码,具体到区县。
	private String receiverRegionCode;    // 收件地址编码	12位国家标准行政区划编码,具体到区县。	
	
	public String getDoType() {
		return doType;
	}
	public void setDoType(String doType) {
		this.doType = doType;
	}
	public String getBrandCode() {
		return brandCode;
	}
	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}
	public String getWaybillNo() {
		return waybillNo;
	}
	public void setWaybillNo(String waybillNo) {
		this.waybillNo = waybillNo;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getIsInternational() {
		return isInternational;
	}
	public void setIsInternational(String isInternational) {
		this.isInternational = isInternational;
	}
	public String getPickupOrgName() {
		return pickupOrgName;
	}
	public void setPickupOrgName(String pickupOrgName) {
		this.pickupOrgName = pickupOrgName;
	}
	public String getPickupOrgCode() {
		return pickupOrgCode;
	}
	public void setPickupOrgCode(String pickupOrgCode) {
		this.pickupOrgCode = pickupOrgCode;
	}
	public Long getPickupTime() {
		return pickupTime;
	}
	public void setPickupTime(Long pickupTime) {
		this.pickupTime = pickupTime;
	}
	public String getPickupAttribute() {
		return pickupAttribute;
	}
	public void setPickupAttribute(String pickupAttribute) {
		this.pickupAttribute = pickupAttribute;
	}
	public String getSenderProvinceNo() {
		return senderProvinceNo;
	}
	public void setSenderProvinceNo(String senderProvinceNo) {
		this.senderProvinceNo = senderProvinceNo;
	}
	public String getRealWeight() {
		return realWeight;
	}
	public void setRealWeight(String realWeight) {
		this.realWeight = realWeight;
	}
	public String getVolWeight() {
		return volWeight;
	}
	public void setVolWeight(String volWeight) {
		this.volWeight = volWeight;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getInnerPropertyCode() {
		return innerPropertyCode;
	}
	public void setInnerPropertyCode(String innerPropertyCode) {
		this.innerPropertyCode = innerPropertyCode;
	}
	public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}
	public String getSenderLinker() {
		return senderLinker;
	}
	public void setSenderLinker(String senderLinker) {
		this.senderLinker = senderLinker;
	}
	public String getSenderMobile() {
		return senderMobile;
	}
	public void setSenderMobile(String senderMobile) {
		this.senderMobile = senderMobile;
	}
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getReceiverLinker() {
		return receiverLinker;
	}
	public void setReceiverLinker(String receiverLinker) {
		this.receiverLinker = receiverLinker;
	}
	public String getReceiverAddress() {
		return receiverAddress;
	}
	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}
	public String getReceiverMobile() {
		return receiverMobile;
	}
	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public String getCodFlag() {
		return codFlag;
	}
	public void setCodFlag(String codFlag) {
		this.codFlag = codFlag;
	}
	public String getCodAmount() {
		return codAmount;
	}
	public void setCodAmount(String codAmount) {
		this.codAmount = codAmount;
	}
	public String getReceiptFlag() {
		return receiptFlag;
	}
	public void setReceiptFlag(String receiptFlag) {
		this.receiptFlag = receiptFlag;
	}
	public String getReceiptWaybillNo() {
		return receiptWaybillNo;
	}
	public void setReceiptWaybillNo(String receiptWaybillNo) {
		this.receiptWaybillNo = receiptWaybillNo;
	}
	public String getInsuranceFlag() {
		return insuranceFlag;
	}
	public void setInsuranceFlag(String insuranceFlag) {
		this.insuranceFlag = insuranceFlag;
	}
	public BigDecimal getPremiumAmount() {
		return premiumAmount;
	}
	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}
	public String getSenderRegionCode() {
		return senderRegionCode;
	}
	public void setSenderRegionCode(String senderRegionCode) {
		this.senderRegionCode = senderRegionCode;
	}
	public String getReceiverRegionCode() {
		return receiverRegionCode;
	}
	public void setReceiverRegionCode(String receiverRegionCode) {
		this.receiverRegionCode = receiverRegionCode;
	}
	
}
