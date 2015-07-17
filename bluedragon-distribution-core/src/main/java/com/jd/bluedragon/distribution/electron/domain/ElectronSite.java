package com.jd.bluedragon.distribution.electron.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

public class ElectronSite extends JdResponse {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -686910726929899142L;

	/**
	 * 主键
	 */
	private Integer id;
	/**
	 * 机构ID
	 */
	private Integer orgId;
	/**
	 * 分拣中心ID
	 */
	private Integer dmsId;
	/**
	 * 任务区
	 */
	private String taskAreaNo;
	
	/**
	 * 主机IP
	 */
	private String ip;
	
	public String getTaskAreaNo() {
		return taskAreaNo;
	}
	public void setTaskAreaNo(String taskAreaNo) {
		this.taskAreaNo = taskAreaNo;
	}
	/**
	 * 货位编码
	 */
	private String goodsPositionNo;
	/**
	 * 电子标签设备码
	 */
	private String electronNo;
	/**
	 * 站点id（多个用逗号隔开）
	 */
	private String siteCodes;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrgId() {
		return orgId;
	}
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}
	public Integer getDmsId() {
		return dmsId;
	}
	public void setDmsId(Integer dmsId) {
		this.dmsId = dmsId;
	}
 
	public String getGoodsPositionNo() {
		return goodsPositionNo;
	}
	public void setGoodsPositionNo(String goodsPositionNo) {
		this.goodsPositionNo = goodsPositionNo;
	}
	public String getElectronNo() {
		return electronNo;
	}
	public void setElectronNo(String electronNo) {
		this.electronNo = electronNo;
	}
	public String getSiteCodes() {
		return siteCodes;
	}
	public void setSiteCodes(String siteCodes) {
		this.siteCodes = siteCodes;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	
 
	
	
}
