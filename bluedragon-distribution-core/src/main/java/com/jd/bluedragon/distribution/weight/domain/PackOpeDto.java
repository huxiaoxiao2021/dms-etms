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
	 * 称量类型（1:分拣中心；2：站点；3:驻场；4:仓储；5:车队；6：运单维度；7：计费操作；8：站点无任务揽收运单维度操作；9：京牛抽检；10：配送交接包裹维度操作；
	 *  11：揽收交接运单维度操作；12：配送交接运单维度操作；13：揽收交接包裹维度操作；）
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
