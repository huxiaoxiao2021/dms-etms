package com.jd.bluedragon.distribution.reverse.domain;

import java.util.Date;

public class LossOrder {

	/** 主键 */
	private Long id;

	/** 订单号 */
	private Long orderId;

	/** 报损单号 */
	private Integer lossCode;

	/** 报损类型 */
	private Integer lossType;

	/** 商品数量 */
	private Integer productQuantity;

	/** 报损数量 */
	private Integer lossQuantity;

	/** 商品编号 */
	private Long productId;

	/** 商品名称 */
	private String productName;

	/** 报损操作人ERP */
	private String userErp;

	/** 报损操作人姓名 */
	private String userName;

	/** 报损时间 */
	private Date lossTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getLossType() {
		return lossType;
	}

	public void setLossType(Integer lossType) {
		this.lossType = lossType;
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

	public Integer getLossQuantity() {
		return lossQuantity;
	}

	public void setLossQuantity(Integer lossQuantity) {
		this.lossQuantity = lossQuantity;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUserErp() {
		return userErp;
	}

	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getLossTime() {
		return this.lossTime == null ? null : (Date) this.lossTime.clone();
	}

	public void setLossTime(Date lossTime) {
		this.lossTime = lossTime == null ? null : (Date) lossTime.clone();
	}

	public Integer getLossCode() {
		return lossCode;
	}

	public void setLossCode(Integer lossCode) {
		this.lossCode = lossCode;
	}

}
