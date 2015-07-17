package com.jd.bluedragon.distribution.failqueue.domain;

public class DealData_SendDatail {
	/*
	 * 发货，取消发货 的字段处理
	 * waybillCode	运单号	String	订单号
	 * sortingCenterId	发货方	int	分拣中心编号
	 * targetSiteId	站点编号	Int	订单对应的目标站点编号
	 * deliveryTime	发货时间 	String 	yyyy-MM-dd hh24:mm:ss
	 */
	private Long primaryKey;
	private String waybillCode;
	private Integer sortingCenterId;
	private Integer targetSiteId;
	private String deliveryTime;
	private String sortBatchNo;
	
	public DealData_SendDatail(){
		
	}
	
	public DealData_SendDatail(
			Long primaryKey,
			String waybillCode,
			Integer sortingCenterId,
			Integer targetSiteId,
			String deliveryTime,
			String sortBatchNo
			){
		this.primaryKey = primaryKey;
		this.waybillCode = waybillCode;
		this.sortingCenterId = sortingCenterId;
		this.targetSiteId = targetSiteId;
		this.deliveryTime = deliveryTime;
		this.sortBatchNo = sortBatchNo;
	}

	public Long getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Long primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getSortingCenterId() {
		return sortingCenterId;
	}

	public void setSortingCenterId(Integer sortingCenterId) {
		this.sortingCenterId = sortingCenterId;
	}

	public Integer getTargetSiteId() {
		return targetSiteId;
	}

	public void setTargetSiteId(Integer targetSiteId) {
		this.targetSiteId = targetSiteId;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getSortBatchNo() {
		return sortBatchNo;
	}

	public void setSortBatchNo(String sortBatchNo) {
		this.sortBatchNo = sortBatchNo;
	}
	
}
