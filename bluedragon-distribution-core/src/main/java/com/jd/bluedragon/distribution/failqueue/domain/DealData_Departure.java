package com.jd.bluedragon.distribution.failqueue.domain;

public class DealData_Departure {
	/**
	 * 分拣中心
	 */
	private Integer sortingCenterId;
	/**
	 * 目标站点
	 */
	private Integer targetSiteId;
	/**
	 * 发车批次
	 */
	private Long sortCarId;//发车批次1
	/**
	 * 承运商编号 
	 */
	private Integer carrierId;
	/**
	 * 发车时间
	 */
	private String sortCarTime;
	/**
	 * 发货批次
	 */
	private String sortBatchNo;//发货批次1，发货批次2
	/**
	 * 重量
	 */
	private Double weight;
	/**
	 * 体积
	 */
	private Double volume;
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
	public Long getSortCarId() {
		return sortCarId;
	}
	public void setSortCarId(Long sortCarId) {
		this.sortCarId = sortCarId;
	}
	public Integer getCarrierId() {
		return carrierId;
	}
	public void setCarrierId(Integer carrierId) {
		this.carrierId = carrierId;
	}
	public String getSortCarTime() {
		return sortCarTime;
	}
	public void setSortCarTime(String sortCarTime) {
		this.sortCarTime = sortCarTime;
	}
	public String getSortBatchNo() {
		return sortBatchNo;
	}
	public void setSortBatchNo(String sortBatchNo) {
		this.sortBatchNo = sortBatchNo;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	
	
}
