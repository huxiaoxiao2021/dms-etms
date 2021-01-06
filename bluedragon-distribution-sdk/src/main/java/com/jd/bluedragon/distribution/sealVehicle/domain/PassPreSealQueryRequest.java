package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;
/**
 * 传摆预封车看板查询条件
 * @author wuyoude
 *
 */
public class PassPreSealQueryRequest implements Serializable{

	private static final long serialVersionUID = -1988125431070900175L;
	/**
	 * 车牌号
	 */
	private String vehicleNumber;
	/**
	 * 始发地编码
	 */
	private Integer originalSiteCode;
	/**
	 * 目的地编码|名称
	 */
	private String destinationSiteCodeOrName;
	/**
	 * 目的滑道号
	 */
	private String destinationCrossCode;
	/**
	 * 发车时间-开始
	 */
	private String departStartTime;
	/**
	 * 发车时间-结束
	 */
	private String departEndTime;
	/**
	 * 查询数据条数
	 */
	private Integer limitNum = 10;
	
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public Integer getOriginalSiteCode() {
		return originalSiteCode;
	}
	public void setOriginalSiteCode(Integer originalSiteCode) {
		this.originalSiteCode = originalSiteCode;
	}
	public String getDestinationSiteCodeOrName() {
		return destinationSiteCodeOrName;
	}
	public void setDestinationSiteCodeOrName(String destinationSiteCodeOrName) {
		this.destinationSiteCodeOrName = destinationSiteCodeOrName;
	}
	public String getDestinationCrossCode() {
		return destinationCrossCode;
	}
	public void setDestinationCrossCode(String destinationCrossCode) {
		this.destinationCrossCode = destinationCrossCode;
	}
	public String getDepartStartTime() {
		return departStartTime;
	}
	public void setDepartStartTime(String departStartTime) {
		this.departStartTime = departStartTime;
	}
	public String getDepartEndTime() {
		return departEndTime;
	}
	public void setDepartEndTime(String departEndTime) {
		this.departEndTime = departEndTime;
	}
	public Integer getLimitNum() {
		return limitNum;
	}
	public void setLimitNum(Integer limitNum) {
		this.limitNum = limitNum;
	}
}
