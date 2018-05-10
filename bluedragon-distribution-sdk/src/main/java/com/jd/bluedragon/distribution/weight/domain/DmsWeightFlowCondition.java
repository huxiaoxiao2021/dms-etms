package com.jd.bluedragon.distribution.weight.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: DmsWeightFlowCondition
 * @Description: 分拣中心称重操作流水-查询条件
 * @author wuyoude
 * @date 2018年03月09日 16:02:53
 *
 */
public class DmsWeightFlowCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 业务类型-1002 */
	private Integer businessType;

	 /** 业务操作类型-100201 */
	private Integer operateType;

	 /** 分拣中心编号 */
	private Integer dmsSiteCode;

	 /** 分拣中心名称 */
	private String dmsSiteName;

	 /** 运单号 */
	private String waybillCode;

	 /** 包裹号 */
	private String packageCode;

	 /** 重量（单位：kg） */
	private Double weight;

	 /** 长度（单位：cm） */
	private Double lenght;

	 /** 宽度（单位：cm） */
	private Double width;

	 /** 高度（单位：cm） */
	private Double high;

	 /** 体积（按运单称重单位：m³，按包裹cm³） */
	private Double volume;

	 /** 操作人code */
	private Integer operatorCode;

	 /** 操作人erp */
	private String operatorErp;

	 /** 操作人名称 */
	private String operatorName;

	 /** 操作时间 */
	private Date operateTime;

	/**
	 * The set method for businessType.
	 * @param businessType
	 */
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	/**
	 * The get method for businessType.
	 * @return this.businessType
	 */
	public Integer getBusinessType() {
		return this.businessType;
	}

	/**
	 * The set method for operateType.
	 * @param operateType
	 */
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	/**
	 * The get method for operateType.
	 * @return this.operateType
	 */
	public Integer getOperateType() {
		return this.operateType;
	}

	/**
	 * The set method for dmsSiteCode.
	 * @param dmsSiteCode
	 */
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}

	/**
	 * The get method for dmsSiteCode.
	 * @return this.dmsSiteCode
	 */
	public Integer getDmsSiteCode() {
		return this.dmsSiteCode;
	}

	/**
	 * The set method for dmsSiteName.
	 * @param dmsSiteName
	 */
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}

	/**
	 * The get method for dmsSiteName.
	 * @return this.dmsSiteName
	 */
	public String getDmsSiteName() {
		return this.dmsSiteName;
	}

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
	 * The set method for packageCode.
	 * @param packageCode
	 */
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	/**
	 * The get method for packageCode.
	 * @return this.packageCode
	 */
	public String getPackageCode() {
		return this.packageCode;
	}

	/**
	 * The set method for weight.
	 * @param weight
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	/**
	 * The get method for weight.
	 * @return this.weight
	 */
	public Double getWeight() {
		return this.weight;
	}

	/**
	 * The set method for lenght.
	 * @param lenght
	 */
	public void setLenght(Double lenght) {
		this.lenght = lenght;
	}

	/**
	 * The get method for lenght.
	 * @return this.lenght
	 */
	public Double getLenght() {
		return this.lenght;
	}

	/**
	 * The set method for width.
	 * @param width
	 */
	public void setWidth(Double width) {
		this.width = width;
	}

	/**
	 * The get method for width.
	 * @return this.width
	 */
	public Double getWidth() {
		return this.width;
	}

	/**
	 * The set method for high.
	 * @param high
	 */
	public void setHigh(Double high) {
		this.high = high;
	}

	/**
	 * The get method for high.
	 * @return this.high
	 */
	public Double getHigh() {
		return this.high;
	}

	/**
	 * The set method for volume.
	 * @param volume
	 */
	public void setVolume(Double volume) {
		this.volume = volume;
	}

	/**
	 * The get method for volume.
	 * @return this.volume
	 */
	public Double getVolume() {
		return this.volume;
	}

	/**
	 * The set method for operatorCode.
	 * @param operatorCode
	 */
	public void setOperatorCode(Integer operatorCode) {
		this.operatorCode = operatorCode;
	}

	/**
	 * The get method for operatorCode.
	 * @return this.operatorCode
	 */
	public Integer getOperatorCode() {
		return this.operatorCode;
	}

	/**
	 * The set method for operatorErp.
	 * @param operatorErp
	 */
	public void setOperatorErp(String operatorErp) {
		this.operatorErp = operatorErp;
	}

	/**
	 * The get method for operatorErp.
	 * @return this.operatorErp
	 */
	public String getOperatorErp() {
		return this.operatorErp;
	}

	/**
	 * The set method for operatorName.
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * The get method for operatorName.
	 * @return this.operatorName
	 */
	public String getOperatorName() {
		return this.operatorName;
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


}
