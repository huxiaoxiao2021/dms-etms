package com.jd.bluedragon.distribution.weight.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 包裹操作数据DTO （称重、测量体积业务需要）
 * @author ligang
 *
 */
public class PackOpeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 运单号
	 */
	private String waybillCode;
	/**
	 * 数据操作环节
	 */
	private Integer opeType;
	/**
	 * 包裹操作明细
	 */
	private List<PackOpeDetail> opeDetails;
	
	
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public Integer getOpeType() {
		return opeType;
	}
	public void setOpeType(Integer opeType) {
		this.opeType = opeType;
	}
	public List<PackOpeDetail> getOpeDetails() {
		return opeDetails;
	}
	public void setOpeDetails(List<PackOpeDetail> opeDetails) {
		this.opeDetails = opeDetails;
	}
	
}
