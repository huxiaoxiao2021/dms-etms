package com.jd.bluedragon.common.dto.strandreport.request;

import java.io.Serializable;

public class ConfigStrandReasonData implements Serializable {
    private static final long serialVersionUID = -1L;

	/**
	 * 原因编码
	 */
	private Integer reasonCode;

	/**
	 * 原因名称
	 */
	private String reasonName;

	/**
	 * 是否同步运单&路由,0-否,1-是
	 */
	private Integer syncFlag;

	/**
	 * 排序值
	 */
	private Integer orderNum;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 滞留原因来源
	 * 1：默认分拣；2：运输
	 */
	private Integer sourceFrom;

	public Integer getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getReasonName() {
		return reasonName;
	}

	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}

	public Integer getSyncFlag() {
		return syncFlag;
	}

	public void setSyncFlag(Integer syncFlag) {
		this.syncFlag = syncFlag;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSourceFrom() {
		return sourceFrom;
	}

	public void setSourceFrom(Integer sourceFrom) {
		this.sourceFrom = sourceFrom;
	}
}
