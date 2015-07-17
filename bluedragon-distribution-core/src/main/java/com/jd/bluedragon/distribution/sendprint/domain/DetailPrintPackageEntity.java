package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;

public class DetailPrintPackageEntity implements Serializable {
	
	private static final long serialVersionUID = 1460940601626738146L;

	/** 箱号 */
	private String boxCode;

	/** 包裹号 */
	private String packageBar;

	/** 订单号 */
	private String waybill;
	
	/** 包裹数量 */
	private String packageBarNum;
	
	/** 包裹重量 */
	private Double packageBarWeight;
	
	/** 订单重量(重复) */
	private Double goodWeight2;
	
	/** 包裹重量(重复) */
	private Double packageBarWeight2;
	
	/** 应收金额 */
	private Double declaredValue;

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getPackageBar() {
		return packageBar;
	}

	public void setPackageBar(String packageBar) {
		this.packageBar = packageBar;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}

	public String getPackageBarNum() {
		return packageBarNum;
	}

	public void setPackageBarNum(String packageBarNum) {
		this.packageBarNum = packageBarNum;
	}

	public Double getPackageBarWeight() {
		return packageBarWeight;
	}

	public void setPackageBarWeight(Double packageBarWeight) {
		this.packageBarWeight = packageBarWeight;
	}

	public Double getGoodWeight2() {
		return goodWeight2;
	}

	public void setGoodWeight2(Double goodWeight2) {
		this.goodWeight2 = goodWeight2;
	}

	public Double getPackageBarWeight2() {
		return packageBarWeight2;
	}

	public void setPackageBarWeight2(Double packageBarWeight2) {
		this.packageBarWeight2 = packageBarWeight2;
	}

	public Double getDeclaredValue() {
		return declaredValue;
	}

	public void setDeclaredValue(Double declaredValue) {
		this.declaredValue = declaredValue;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
