package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: TmsVehicleRoute
 * @Description: 运输任务线路表-查询条件
 * @author wuyoude
 * @date 2020年12月31日 21:24:16
 *
 */
public class TmsVehicleRouteCondition implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 始发站点编码
	 */
	private Integer originalSiteCode;

	/**
	 * 目的站点编码
	 */
	private Integer destinationSiteCode;
	
	/**
	 * 车牌号
	 */
	private String vehicleNumber;

	/**
	 * 发车时间-开始
	 */
	private Date departStartTime;
	
	/**
	 * 发车时间
	 */
	private Date departEndTime;

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

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public Date getDepartStartTime() {
		return departStartTime;
	}

	public void setDepartStartTime(Date departStartTime) {
		this.departStartTime = departStartTime;
	}

	public Date getDepartEndTime() {
		return departEndTime;
	}

	public void setDepartEndTime(Date departEndTime) {
		this.departEndTime = departEndTime;
	}

}
