package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: TmsVehicleRoute
 * @Description: 运输任务线路表-实体类
 * @author wuyoude
 * @date 2020年12月31日 21:24:16
 *
 */
public class TmsVehicleRoute implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 车次任务编码
	 */
	private String vehicleJobCode;

	/**
	 * 车次任务线路编码
	 */
	private String vehicleRouteCode;

	/**
	 * 运力编码
	 */
	private String transportCode;

	/**
	 * 始发站点编码
	 */
	private Integer originalSiteCode;

	/**
	 * 目的站点编码
	 */
	private Integer destinationSiteCode;

	/**
	 * 发车时间
	 */
	private Date departTime;

	/**
	 * 车队编码
	 */
	private String carrierTeamCode;

	/**
	 * 车队名称
	 */
	private String carrierTeamName;

	/**
	 * 车牌号
	 */
	private String vehicleNumber;

	/**
	 * 任务创建时间
	 */
	private Date jobCreateTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 取消时间
	 */
	private Date cancelTime;

	/**
	 * 有效标识：1-有效 0-无效
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 * @return id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 *
	 * @param vehicleJobCode
	 */
	public void setVehicleJobCode(String vehicleJobCode) {
		this.vehicleJobCode = vehicleJobCode;
	}

	/**
	 *
	 * @return vehicleJobCode
	 */
	public String getVehicleJobCode() {
		return this.vehicleJobCode;
	}

	/**
	 *
	 * @param vehicleRouteCode
	 */
	public void setVehicleRouteCode(String vehicleRouteCode) {
		this.vehicleRouteCode = vehicleRouteCode;
	}

	/**
	 *
	 * @return vehicleRouteCode
	 */
	public String getVehicleRouteCode() {
		return this.vehicleRouteCode;
	}

	/**
	 *
	 * @param transportCode
	 */
	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	/**
	 *
	 * @return transportCode
	 */
	public String getTransportCode() {
		return this.transportCode;
	}

	/**
	 *
	 * @param originalSiteCode
	 */
	public void setOriginalSiteCode(Integer originalSiteCode) {
		this.originalSiteCode = originalSiteCode;
	}

	/**
	 *
	 * @return originalSiteCode
	 */
	public Integer getOriginalSiteCode() {
		return this.originalSiteCode;
	}

	/**
	 *
	 * @param destinationSiteCode
	 */
	public void setDestinationSiteCode(Integer destinationSiteCode) {
		this.destinationSiteCode = destinationSiteCode;
	}

	/**
	 *
	 * @return destinationSiteCode
	 */
	public Integer getDestinationSiteCode() {
		return this.destinationSiteCode;
	}

	/**
	 *
	 * @param departTime
	 */
	public void setDepartTime(Date departTime) {
		this.departTime = departTime;
	}

	/**
	 *
	 * @return departTime
	 */
	public Date getDepartTime() {
		return this.departTime;
	}

	/**
	 *
	 * @param carrierTeamCode
	 */
	public void setCarrierTeamCode(String carrierTeamCode) {
		this.carrierTeamCode = carrierTeamCode;
	}

	/**
	 *
	 * @return carrierTeamCode
	 */
	public String getCarrierTeamCode() {
		return this.carrierTeamCode;
	}

	/**
	 *
	 * @param carrierTeamName
	 */
	public void setCarrierTeamName(String carrierTeamName) {
		this.carrierTeamName = carrierTeamName;
	}

	/**
	 *
	 * @return carrierTeamName
	 */
	public String getCarrierTeamName() {
		return this.carrierTeamName;
	}

	/**
	 *
	 * @param vehicleNumber
	 */
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	/**
	 *
	 * @return vehicleNumber
	 */
	public String getVehicleNumber() {
		return this.vehicleNumber;
	}

	/**
	 *
	 * @param jobCreateTime
	 */
	public void setJobCreateTime(Date jobCreateTime) {
		this.jobCreateTime = jobCreateTime;
	}

	/**
	 *
	 * @return jobCreateTime
	 */
	public Date getJobCreateTime() {
		return this.jobCreateTime;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param cancelTime
	 */
	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	/**
	 *
	 * @return cancelTime
	 */
	public Date getCancelTime() {
		return this.cancelTime;
	}

	/**
	 *
	 * @param yn
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Integer getYn() {
		return this.yn;
	}

	/**
	 *
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 *
	 * @return ts
	 */
	public Date getTs() {
		return this.ts;
	}


}
