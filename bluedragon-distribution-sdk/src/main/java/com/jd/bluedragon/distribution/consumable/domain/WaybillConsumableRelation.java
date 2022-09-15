package com.jd.bluedragon.distribution.consumable.domain;

import java.math.BigDecimal;
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

	/**
	 * 新增明细字段：耗材名称
	 */
	private String consumableName;

	/**
	 * 新增明细字段：耗材类型
	 */
	private String consumableType;

	/**
	 * 新增明细字段：耗材类型名称
	 */
	private String consumableTypeName;

	/**
	 * 新增明细字段：体积(cm³)
	 */
	private BigDecimal volume;

	/**
	 * 新增明细字段：体积系数
	 */
	private BigDecimal volumeCoefficient;

	/**
	 * 新增明细字段：规格
	 */
	private String specification;

	/**
	 * 新增明细字段：单位
	 */
	private String unit;

	/**
	 * 新增明细字段：重量
	 */
	private BigDecimal weight;

	/**
	 * 包装耗材价格
	 */
	private BigDecimal packingCharge;

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

	/** 确认体积：打包装后体积（m³） **/
	private Double confirmVolume;

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

	public String getConsumableName() {
		return consumableName;
	}

	public void setConsumableName(String consumableName) {
		this.consumableName = consumableName;
	}

	public String getConsumableType() {
		return consumableType;
	}

	public void setConsumableType(String consumableType) {
		this.consumableType = consumableType;
	}

	public String getConsumableTypeName() {
		return consumableTypeName;
	}

	public void setConsumableTypeName(String consumableTypeName) {
		this.consumableTypeName = consumableTypeName;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getVolumeCoefficient() {
		return volumeCoefficient;
	}

	public void setVolumeCoefficient(BigDecimal volumeCoefficient) {
		this.volumeCoefficient = volumeCoefficient;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getPackingCharge() {
		return packingCharge;
	}

	public void setPackingCharge(BigDecimal packingCharge) {
		this.packingCharge = packingCharge;
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

	public Double getConfirmVolume() {
		return confirmVolume;
	}

	public void setConfirmVolume(Double confirmVolume) {
		this.confirmVolume = confirmVolume;
	}
}
