package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Date;
import java.math.BigDecimal;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: PackingConsumableInfo
 * @Description: 包装耗材信息表-实体类
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public class PackingConsumableInfo extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 耗材编号 */
	private String code;

	 /** 耗材名称 */
	private String name;

	 /** 类型 */
	private String type;

	/** 类型名称 */
	private String typeName;

	 /** 体积 */
	private BigDecimal volume;

	 /** 体积系数 */
	private BigDecimal volumeCoefficient;

	 /** 规格 */
	private String specification;

	 /** 单位 */
	private String unit;

	 /** 操作人编号 */
	private String operateUserCode;

	 /** 操作人erp */
	private String operateUserErp;

	 /** 操作时间 */
	private Date operateTime;

    /**
     * 重量(KG)
     */
	private BigDecimal weight;

	public PackingConsumableInfo() {
	}

	/**
	 * The set method for code.
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * The get method for code.
	 * @return this.code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * The set method for name.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The get method for name.
	 * @return this.name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The set method for type.
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The get method for type.
	 * @return this.type
	 */
	public String getType() {
		return this.type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * The set method for volume.
	 * @param volume
	 */
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	/**
	 * The get method for volume.
	 * @return this.volume
	 */
	public BigDecimal getVolume() {
		return this.volume;
	}

	/**
	 * The set method for volumeCoefficient.
	 * @param volumeCoefficient
	 */
	public void setVolumeCoefficient(BigDecimal volumeCoefficient) {
		this.volumeCoefficient = volumeCoefficient;
	}

	/**
	 * The get method for volumeCoefficient.
	 * @return this.volumeCoefficient
	 */
	public BigDecimal getVolumeCoefficient() {
		return this.volumeCoefficient;
	}

	/**
	 * The set method for specification.
	 * @param specification
	 */
	public void setSpecification(String specification) {
		this.specification = specification;
	}

	/**
	 * The get method for specification.
	 * @return this.specification
	 */
	public String getSpecification() {
		return this.specification;
	}

	/**
	 * The set method for unit.
	 * @param unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * The get method for unit.
	 * @return this.unit
	 */
	public String getUnit() {
		return this.unit;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
