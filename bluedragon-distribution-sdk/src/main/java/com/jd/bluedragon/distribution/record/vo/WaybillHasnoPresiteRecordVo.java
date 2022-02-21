package com.jd.bluedragon.distribution.record.vo;

import java.io.Serializable;

import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;

/**
 * Description: 无滑道包裹明细<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 */
public class WaybillHasnoPresiteRecordVo extends WaybillHasnoPresiteRecord implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1902751845256421630L;

	/**
     * 状态
     */
    private String statusDesc;
	/**
     * 外呼状态
     */
    private String callStatusDesc;

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getCallStatusDesc() {
		return callStatusDesc;
	}

	public void setCallStatusDesc(String callStatusDesc) {
		this.callStatusDesc = callStatusDesc;
	}

}
