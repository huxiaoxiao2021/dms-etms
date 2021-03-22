package com.jd.bluedragon.distribution.newseal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * https://cf.jd.com/pages/viewpage.action?pageId=443178630
 * @ClassName: TmsVehicleJobCancelMq
 * @Description: 传摆车次任务取消MQ
 * @author wuyoude
 * @date 2021年02月25日 16:45:40
 *
 */
public class TmsVehicleJobCancelMq implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 车次任务编码
	 */
	private String vehicleJobCode;
	/**
	 * 状态：200-已取消
	 */
	private Integer status;
	/**
	 * 操作时间
	 */
	private Date operateTime;
	/**
	 * 操作人账号
	 */
	private String operateUserCode;
	/**
	 * 操作人姓名
	 */
	private String operateUserName;
	
	public String getVehicleJobCode() {
		return vehicleJobCode;
	}
	public void setVehicleJobCode(String vehicleJobCode) {
		this.vehicleJobCode = vehicleJobCode;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public String getOperateUserCode() {
		return operateUserCode;
	}
	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}
	public String getOperateUserName() {
		return operateUserName;
	}
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}
}
