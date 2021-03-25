package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 传摆预封车看板查询结果
 * @author wuyoude
 *
 */
public class PassPreSealRecord implements Serializable{

	private static final long serialVersionUID = 6899641798918520802L;
	/**
	 * 任务编码
	 */
	private String vehicleJobCode;
	/**
	 * 运力编码
	 */
	private String transportCode;
	/**
	 * 车牌号
	 */
	private String vehicleNumber;
	/**
	 * 始发地
	 */
	private Integer originalSiteCode;
	/**
	 * 始发地
	 */
	private String originalSiteName;
	/**
	 * 目的地
	 */
	private Integer destinationSiteCode;
	/**
	 * 目的地
	 */
	private String destinationSiteName;
	/**
	 * 目的滑道号
	 */
	private String destinationCrossCode;
	/**
	 * 任务创建时间
	 */
	private Date jobCreateTime;
	/**
	 * 车队名称
	 */
	private String carrierTeamName;
	/**
	 * 是否预封车
	 */
	private String preSealStatus;
	/**
	 * 标准发车时间
	 */
	private Date departTime;
	
	public String getVehicleJobCode() {
		return vehicleJobCode;
	}
	public void setVehicleJobCode(String vehicleJobCode) {
		this.vehicleJobCode = vehicleJobCode;
	}
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public String getOriginalSiteName() {
		return originalSiteName;
	}
	public void setOriginalSiteName(String originalSiteName) {
		this.originalSiteName = originalSiteName;
	}
	public String getDestinationSiteName() {
		return destinationSiteName;
	}
	public void setDestinationSiteName(String destinationSiteName) {
		this.destinationSiteName = destinationSiteName;
	}
	public String getDestinationCrossCode() {
		return destinationCrossCode;
	}
	public void setDestinationCrossCode(String destinationCrossCode) {
		this.destinationCrossCode = destinationCrossCode;
	}
	public Date getJobCreateTime() {
		return jobCreateTime;
	}
	public void setJobCreateTime(Date jobCreateTime) {
		this.jobCreateTime = jobCreateTime;
	}
	public String getCarrierTeamName() {
		return carrierTeamName;
	}
	public void setCarrierTeamName(String carrierTeamName) {
		this.carrierTeamName = carrierTeamName;
	}
	public String getPreSealStatus() {
		return preSealStatus;
	}
	public void setPreSealStatus(String preSealStatus) {
		this.preSealStatus = preSealStatus;
	}
	public Date getDepartTime() {
		return departTime;
	}
	public void setDepartTime(Date departTime) {
		this.departTime = departTime;
	}
	public Integer getOriginalSiteCode() {
		return originalSiteCode;
	}
	public void setOriginalSiteCode(Integer originalSiteCode) {
		this.originalSiteCode = originalSiteCode;
	}
	public Integer getDestinationSiteCode() {
		return destinationSiteCode;
	}
	public void setDestinationSiteCode(Integer destinationSiteCode) {
		this.destinationSiteCode = destinationSiteCode;
	}
	public String getTransportCode() {
		return transportCode;
	}
	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}
}
