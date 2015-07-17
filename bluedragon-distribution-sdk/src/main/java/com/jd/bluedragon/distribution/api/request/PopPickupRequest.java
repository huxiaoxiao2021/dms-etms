package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class PopPickupRequest extends JdRequest{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4414530169841597578L;
	/*pop商家编号*/
	private String popBusinessCode;
	/*pop商家名称*/
	private String popBusinessName;
	/*运单号*/
	private String waybillCode;
	/*包裹号*/
	private String packageBarcode;
	/*运单包含的包裹数量*/
	private Integer packageNumber;
	/*车牌号*/
	private String carCode;
	/*是否取消 1：取消 0：未取消*/
	private Integer isCancel;
	
	/**
	 * 运单类型
	 */
	private Integer waybillType;
	
	/**
	 * 类型，驻厂接货为 5
	 */
	private Integer pickupType;
	
	public String getPopBusinessCode() {
		return popBusinessCode;
	}
	public void setPopBusinessCode(String popBusinessCode) {
		this.popBusinessCode = popBusinessCode;
	}
	public String getPopBusinessName() {
		return popBusinessName;
	}
	public void setPopBusinessName(String popBusinessName) {
		this.popBusinessName = popBusinessName;
	}
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public String getPackageBarcode() {
		return packageBarcode;
	}
	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}
	public Integer getPackageNumber() {
		return packageNumber;
	}
	public void setPackageNumber(Integer packageNumber) {
		this.packageNumber = packageNumber;
	}
	public String getCarCode() {
		return carCode;
	}
	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}
	public Integer getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(Integer isCancel) {
		this.isCancel = isCancel;
	}
	public Integer getWaybillType() {
		return waybillType;
	}
	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}
	public Integer getPickupType() {
		return pickupType;
	}
	public void setPickupType(Integer pickupType) {
		this.pickupType = pickupType;
	}
	
}
