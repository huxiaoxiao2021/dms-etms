package com.jd.bluedragon.distribution.urban.domain;

import java.util.Date;
import com.jd.bluedragon.common.domain.DbEntity;

/**
 * 城配运单同步表-实体类
 * 
 * @ClassName: UrbanWaybill
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月20日 10:42:05
 *
 */
public class UrbanWaybill extends DbEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5786145169611193415L;

	/**
	 * 运单号
	 */
	private String waybillCode;

	/**
	 * 调度单号
	 */
	private String scheduleBillCode;

	/**
	 * 运输单号
	 */
	private String transbillCode;

	/**
	 * 运输单状态
	 */
	private Integer status;
	/**
	 * 操作人code
	 */
	private String operateUserCode;
	/**
	 * 操作人ERP
	 */
	private String operateUserErp;

	/**
	 * 操作人姓名
	 */
	private String operateUserName;

	/**
	 * 操作时间
	 */
	private Date operateTime;

	/**
	 *
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 *
	 * @return waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 *
	 * @param scheduleBillCode
	 */
	public void setScheduleBillCode(String scheduleBillCode) {
		this.scheduleBillCode = scheduleBillCode;
	}

	/**
	 *
	 * @return scheduleBillCode
	 */
	public String getScheduleBillCode() {
		return this.scheduleBillCode;
	}

	/**
	 *
	 * @param transbillCode
	 */
	public void setTransbillCode(String transbillCode) {
		this.transbillCode = transbillCode;
	}

	/**
	 *
	 * @return transbillCode
	 */
	public String getTransbillCode() {
		return this.transbillCode;
	}

	/**
	 *
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 *
	 * @return status
	 */
	public Integer getStatus() {
		return this.status;
	}

	public String getOperateUserCode() {
		return operateUserCode;
	}

	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}

	/**
	 *
	 * @param operateUserErp
	 */
	public void setOperateUserErp(String operateUserErp) {
		this.operateUserErp = operateUserErp;
	}

	/**
	 *
	 * @return operateUserErp
	 */
	public String getOperateUserErp() {
		return this.operateUserErp;
	}

	/**
	 *
	 * @param operateUserName
	 */
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}

	/**
	 *
	 * @return operateUserName
	 */
	public String getOperateUserName() {
		return this.operateUserName;
	}

	/**
	 *
	 * @param operateTime
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 *
	 * @return operateTime
	 */
	public Date getOperateTime() {
		return this.operateTime;
	}

}
