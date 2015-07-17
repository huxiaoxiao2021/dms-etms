package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;

/**
 * 根据发车号, 三方运单号查询得到的运单的明细
 * @author zhuchao
 *
 */
public class DepartureWaybillDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String waybillCode;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

}
