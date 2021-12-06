package com.jd.bluedragon.distribution.record.entity;

import java.io.Serializable;
import java.util.Date;

import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;

/**
 * 
 * @author wuyoude
 *
 */
public class DmsHasnoPresiteWaybillMq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 运单号
	 */
	private String waybillCode;

	/**
	 * 预分拣站点编码
	 */
	private Integer presiteCode;
	/**
	 * 目的分拣中心id
	 */
	private Integer endDmsId;
	/**
	 * 操作码
	 */
	private Integer operateCode = DmsHasnoPresiteWaybillMqOperateEnum.INIT.getCode();
	/**
	 * 操作站点
	 */
	private Integer operateSiteCode;
	/**
	 * 操作人Erp
	 */
	private String operateUserErp;
	/**
	 * 操作人名称
	 */
	private String operateUserName;
	/**
	 * 操作时间
	 */
	private Date operateTime;
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public Integer getPresiteCode() {
		return presiteCode;
	}
	public void setPresiteCode(Integer presiteCode) {
		this.presiteCode = presiteCode;
	}
	public Integer getEndDmsId() {
		return endDmsId;
	}
	public void setEndDmsId(Integer endDmsId) {
		this.endDmsId = endDmsId;
	}
	public Integer getOperateCode() {
		return operateCode;
	}
	public void setOperateCode(Integer operateCode) {
		this.operateCode = operateCode;
	}
	public Integer getOperateSiteCode() {
		return operateSiteCode;
	}
	public void setOperateSiteCode(Integer operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}
	public String getOperateUserErp() {
		return operateUserErp;
	}
	public void setOperateUserErp(String operateUserErp) {
		this.operateUserErp = operateUserErp;
	}
	public String getOperateUserName() {
		return operateUserName;
	}
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
}
