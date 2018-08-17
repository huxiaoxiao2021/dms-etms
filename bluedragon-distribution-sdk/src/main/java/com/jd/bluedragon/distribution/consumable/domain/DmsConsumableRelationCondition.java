package com.jd.bluedragon.distribution.consumable.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 *
 * @ClassName: DmsConsumableRelationCondition
 * @Description: 分拣中心耗材关系表-查询条件
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public class DmsConsumableRelationCondition extends BasePagerCondition {

	private static final long serialVersionUID = 1L;

	 /** 分拣中心编号 */
	private Integer dmsId;

	 /** 分拣中心名称 */
	private String dmsName;

	 /** 耗材编号 */
	private String consumableCode;

	 /** 开启状态（0关闭，1开启） */
	private Integer status;

	 /** 操作人编号 */
	private String operateUserCode;

	 /** 操作人erp */
	private String operateUserErp;

	 /** 操作时间 */
	private Date operateTime;

	/**
	 * The set method for dmsId.
	 * @param dmsId
	 */
	public void setDmsId(Integer dmsId) {
		this.dmsId = dmsId;
	}

	/**
	 * The get method for dmsId.
	 * @return this.dmsId
	 */
	public Integer getDmsId() {
		return this.dmsId;
	}

	/**
	 * The set method for dmsName.
	 * @param dmsName
	 */
	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	/**
	 * The get method for dmsName.
	 * @return this.dmsName
	 */
	public String getDmsName() {
		return this.dmsName;
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
	 * The set method for status.
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * The get method for status.
	 * @return this.status
	 */
	public Integer getStatus() {
		return this.status;
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


}
