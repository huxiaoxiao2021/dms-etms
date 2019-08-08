package com.jd.bluedragon.distribution.alliance.domain;

import java.util.Date;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 *
 * @ClassName: AllianceBusiDeliveryDetail
 * @Description: 加盟商计费交付明细表-实体类
 * @author wuyoude
 * @date 2019年07月10日 15:35:36
 *
 */
public class AllianceBusiDeliveryDetail extends DbEntity {

	private static final long serialVersionUID = 1L;

	 /** 运单号 */
	private String waybillCode;

	 /** 包裹号 */
	private String packageCode;

	 /** 系统来源 */
	private Integer sysSource;

	 /** 操作时间 */
	private Date operateTime;

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
	 * The set method for sysSource.
	 * @param sysSource
	 */
	public void setSysSource(Integer sysSource) {
		this.sysSource = sysSource;
	}

	/**
	 * The get method for sysSource.
	 * @return this.sysSource
	 */
	public Integer getSysSource() {
		return this.sysSource;
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
