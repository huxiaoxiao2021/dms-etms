package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.ArrayList;

public class OrderResponse extends JdResponse {

	private static final long serialVersionUID = 3894035408194340958L;

	/** 订单编号 */
	private String waybillCode;

	/** cky2 */
	private Integer cky2;

	/** 客户地址 */
	private String address;

	/** 重新分配地址 */
	private String reassignAddress;

	/** 客户编码 */
	private String mobile;

	/** 预分拣站点编码 */
	private Integer siteId;

	/** 预分拣站点名称 */
	private String siteName;

	/** 支付方式 */
	private Integer payment;

	/** 支付方式文字 **/
	private String paymentText;

	/** 订单类型 */
	private Integer waybillType;

	/** 订单类型文字 **/
	private String waybillTypeText;

	/** 包裹数量 */
	private Integer packageQuantity;

	/** 商品总数量 */
	private String sendPay;

	/** 中转站编号 */
	private Integer transferStationId;

	/** 中转站名称 */
	private String transferStationName;

	/** 商品总数量 */
	private Integer productQuantity;

	/** 商家ID */
	private String popSupNo;

	/** 商家名称 */
	private String popSupName;
	
	/** 订单状态 */
	private Integer Status;

	/** 包裹号集合 */
	private ArrayList<OrderPackage> packages = new ArrayList<OrderPackage>();

	private Product product;

	public OrderResponse() {
	}

	public OrderResponse(Integer code, String message) {
		super(code, message);
	}

	public Integer getTransferStationId() {
		return transferStationId;
	}

	public void setTransferStationId(Integer transferStationId) {
		this.transferStationId = transferStationId;
	}

	public String getTransferStationName() {
		return transferStationName;
	}

	public void setTransferStationName(String transferStationName) {
		this.transferStationName = transferStationName;
	}

	public String getSendPay() {
		return sendPay;
	}

	public void setSendPay(String sendpay) {
		this.sendPay = sendpay;
	}

	public ArrayList<OrderPackage> getPackages() {
		return packages;
	}

	public void setPackages(ArrayList<OrderPackage> packageD) {
		this.packages = packageD;
	}

	public void addPackage(OrderPackage apackage) {
		if (this.packages == null) {
			this.packages = new ArrayList<OrderPackage>();
		}

		this.packages.add(apackage);
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getCky2() {
		return cky2;
	}

	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

	public String getPaymentText() {
		return paymentText;
	}

	public void setPaymentText(String paymentText) {
		this.paymentText = paymentText;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public String getWaybillTypeText() {
		return waybillTypeText;
	}

	public void setWaybillTypeText(String waybillTypeText) {
		this.waybillTypeText = waybillTypeText;
	}

	public Integer getPackageQuantity() {
		return packageQuantity;
	}

	public void setPackageQuantity(Integer packageQuantity) {
		this.packageQuantity = packageQuantity;
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

	public String getPopSupNo() {
		return popSupNo;
	}

	public void setPopSupNo(String popSupNo) {
		this.popSupNo = popSupNo;
	}

	public String getPopSupName() {
		return popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public String getReassignAddress() {
		return reassignAddress;
	}

	public void setReassignAddress(String reassignAddress) {
		this.reassignAddress = reassignAddress;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getStatus() {
		return Status;
	}

	public void setStatus(Integer status) {
		Status = status;
	}

	
}
