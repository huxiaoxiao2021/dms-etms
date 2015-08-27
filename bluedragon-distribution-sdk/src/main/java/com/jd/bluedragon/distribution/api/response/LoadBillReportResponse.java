package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

public class LoadBillReportResponse implements Serializable {

	private static final long serialVersionUID = 1213634666096239241L;

	/** 接收状态: 1：成功，2：失败 */
	private Integer status;

	/** 如失败，则放错误信息 */
	private String notes;

	public LoadBillReportResponse() {
		super();
	}

	public LoadBillReportResponse(Integer status, String notes) {
		super();
		this.status = status;
		this.notes = notes;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
