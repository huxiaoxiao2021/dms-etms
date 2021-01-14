package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: TmsVehicleRouteMq
 * @Description: 运输任务线路mq
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
public class TmsVehicleRouteMq implements Serializable {

	private static final long serialVersionUID = 1L;
	
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
	 * 操作类型 10-创建，20-取消
	 */
	private Integer operateType;

	/**
	 * 车队编码
	 */
	private String carrierTeamCode;

	/**
	 * 车队名称
	 */
	private String carrierTeamName;
	/**
	 * 司机编码
	 */
	private String carrierDriverCode;

	/**
	 * 司机名称
	 */
	private String carrierDriverName;
	/**
	 * 车牌号
	 */
	private String vehicleNumber;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 取消时间
	 */
	private Date cancelTime;
	
	/**
	 * 创建时间
	 */
	private Date jobCreateTime;
	/**
	 * 始发网点编码
	 */
	private String beginNodeCode;
	/**
	 * 始发网点编码
	 */
	private String endNodeCode;

	public String getVehicleJobCode() {
		return vehicleJobCode;
	}

	public void setVehicleJobCode(String vehicleJobCode) {
		this.vehicleJobCode = vehicleJobCode;
	}

	public String getVehicleRouteCode() {
		return vehicleRouteCode;
	}

	public void setVehicleRouteCode(String vehicleRouteCode) {
		this.vehicleRouteCode = vehicleRouteCode;
	}

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public String getCarrierTeamCode() {
		return carrierTeamCode;
	}

	public void setCarrierTeamCode(String carrierTeamCode) {
		this.carrierTeamCode = carrierTeamCode;
	}

	public String getCarrierTeamName() {
		return carrierTeamName;
	}

	public void setCarrierTeamName(String carrierTeamName) {
		this.carrierTeamName = carrierTeamName;
	}

	public String getCarrierDriverCode() {
		return carrierDriverCode;
	}

	public void setCarrierDriverCode(String carrierDriverCode) {
		this.carrierDriverCode = carrierDriverCode;
	}

	public String getCarrierDriverName() {
		return carrierDriverName;
	}

	public void setCarrierDriverName(String carrierDriverName) {
		this.carrierDriverName = carrierDriverName;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCancelTime() {
		return cancelTime;
	}

	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	public Date getJobCreateTime() {
		return jobCreateTime;
	}

	public void setJobCreateTime(Date jobCreateTime) {
		this.jobCreateTime = jobCreateTime;
	}

	public String getBeginNodeCode() {
		return beginNodeCode;
	}

	public void setBeginNodeCode(String beginNodeCode) {
		this.beginNodeCode = beginNodeCode;
	}

	public String getEndNodeCode() {
		return endNodeCode;
	}

	public void setEndNodeCode(String endNodeCode) {
		this.endNodeCode = endNodeCode;
	}
}
