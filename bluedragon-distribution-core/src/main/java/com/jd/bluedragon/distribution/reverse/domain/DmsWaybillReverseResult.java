package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
/**
 * 分拣对象DmsWaybillReverseResult
 * @author wuyoude
 *
 */
public class DmsWaybillReverseResult implements Serializable {
	private static final long serialVersionUID = -9099322344572526715L;
	
	private String waybillCode;

	public DmsWaybillReverseResult() {
	}

	public DmsWaybillReverseResult(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getWaybillCode() {
		return this.waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
}