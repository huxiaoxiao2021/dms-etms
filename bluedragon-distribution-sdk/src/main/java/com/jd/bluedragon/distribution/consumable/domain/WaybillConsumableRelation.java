package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: WaybillConsumableRelation
 * @Description: 运单耗材关系表-实体类
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public class WaybillConsumableRelation extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 运单号 */
	private String waybillCode;

	 /** 耗材编号 */
	private String consumableCode;

	 /** 揽收数量 */
	private Double receiveQuantity;

	 /** 确认数量 */
	private Double confirmQuantity;

	 /** 操作人编号 */
	private String operateUserCode;

	 /** 操作人erp */
	private String operateUserErp;

	 /** 操作时间 */
	private Date operateTime;

	/** 打包人erp */
	private String packUserErp;

	/**
	 * The set method for waybillCode.
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * The get method for waybillCode.
	 * @return this.waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 * The set method for consumableCode.
	 * @param consumableCode
	 */
	public void setConsumableCode(String consumableCode) {
		this.consumableCode = consumableCode;
	}

	/**
	 * The get method for consumableCode.
	 * @return this.consumableCode
	 */
	public String getConsumableCode() {
		return this.consumableCode;
	}

	/**
	 * The set method for receiveQuantity.
	 * @param receiveQuantity
	 */
	public void setReceiveQuantity(Double receiveQuantity) {
		this.receiveQuantity = receiveQuantity;
	}

	/**
	 * The get method for receiveQuantity.
	 * @return this.receiveQuantity
	 */
	public Double getReceiveQuantity() {
		return this.receiveQuantity;
	}

	/**
	 * The set method for confirmQuantity.
	 * @param confirmQuantity
	 */
	public void setConfirmQuantity(Double confirmQuantity) {
		this.confirmQuantity = confirmQuantity;
	}

	/**
	 * The get method for confirmQuantity.
	 * @return this.confirmQuantity
	 */
	public Double getConfirmQuantity() {
		return this.confirmQuantity;
	}

	/**
	 * The set method for operateUserCode.
	 * @param operateUserCode
	 */
	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}

	/**
	 * The get method for operateUserCode.
	 * @return this.operateUserCode
	 */
	public String getOperateUserCode() {
		return this.operateUserCode;
	}

	/**
	 * The set method for operateUserErp.
	 * @param operateUserErp
	 */
	public void setOperateUserErp(String operateUserErp) {
		this.operateUserErp = operateUserErp;
	}

	/**
	 * The get method for operateUserErp.
	 * @return this.operateUserErp
	 */
	public String getOperateUserErp() {
		return this.operateUserErp;
	}

	/**
	 * The set method for operateTime.
	 * @param operateTime
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 * The get method for operateTime.
	 * @return this.operateTime
	 */
	public Date getOperateTime() {
		return this.operateTime;
	}

	public String getPackUserErp() {
		return packUserErp;
	}

	public void setPackUserErp(String packUserErp) {
		this.packUserErp = packUserErp;
	}
}
