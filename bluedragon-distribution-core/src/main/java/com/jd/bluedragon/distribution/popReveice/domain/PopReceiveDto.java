package com.jd.bluedragon.distribution.popReveice.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午09:11:19
 *
 * 类说明
 */
public class PopReceiveDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	/**
	 * 三方运单号
	 */
	private String thirdWaybillCode;
	
	
	/**
	 * 机构
	 */
	private Integer orgCode;

	/**
	 * 创建站点
	 */
	private Integer createSiteCode;
	
	private String operateStartTime;
	private String operateEndTime;
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	private Date endTime;
	
	/**
	 * 打印包裹或打印发票？
	 * 1：打印包裹
	 * 2：打印发票
	 */
	private Integer isPackOrInvoice;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Date getStartTime() {
		return startTime != null ? (Date)startTime.clone() : null;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime != null ? (Date)startTime.clone() : null;
	}

	public Date getEndTime() {
		return endTime != null ? (Date)endTime.clone() : null;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime != null ? (Date)endTime.clone() : null;
	}

	public Integer getIsPackOrInvoice() {
		return isPackOrInvoice;
	}

	public void setIsPackOrInvoice(Integer isPackOrInvoice) {
		this.isPackOrInvoice = isPackOrInvoice;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public String getOperateStartTime() {
		return operateStartTime;
	}

	public void setOperateStartTime(String operateStartTime) {
		this.operateStartTime = operateStartTime;
	}

	public String getOperateEndTime() {
		return operateEndTime;
	}

	public void setOperateEndTime(String operateEndTime) {
		this.operateEndTime = operateEndTime;
	}
	
	
}
