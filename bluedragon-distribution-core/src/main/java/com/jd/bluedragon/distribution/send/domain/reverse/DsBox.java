package com.jd.bluedragon.distribution.send.domain.reverse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DsBox")
public class DsBox {
	
	private int id = 0;
	private int driverId = 0;
	private int operatorId = 0;
	private int orderSum = 0;
	private int orgId = 0;
	private int siteId = 0;
	private int sourceDCId = 0;
	private String batchId = "";
	private String boxId = "";
	private String driverName = "";
	private String operatorName = "";
	/** 目的站点类型 64：异地分拣中心 900：售后 */
	private String remark = "";
	private Date sendTime = new Date();
	private String siteName = "";
	private String carLicense = "";
	private String destId = "";
	private List<DsOrder> orderList = new ArrayList<DsOrder>();
	
	public String getBatchId() {
		return this.batchId;
	}
	
	public String getBoxId() {
		return this.boxId;
	}
	
	public String getCarLicense() {
		return this.carLicense;
	}
	
	public String getDestId() {
		return this.destId;
	}
	
	public int getDriverId() {
		return this.driverId;
	}
	
	public String getDriverName() {
		return this.driverName;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getOperatorId() {
		return this.operatorId;
	}
	
	public String getOperatorName() {
		return this.operatorName;
	}
	
	public List<DsOrder> getOrderList() {
		return this.orderList;
	}
	
	public int getOrderSum() {
		return this.orderSum;
	}
	
	public int getOrgId() {
		return this.orgId;
	}
	
	public String getRemark() {
		return this.remark;
	}
	
	/**
	 * @return the sendTime
	 */
	public Date getSendTime() {
		return this.sendTime == null ? null : (Date) this.sendTime.clone();
	}
	
	public int getSiteId() {
		return this.siteId;
	}
	
	public String getSiteName() {
		return this.siteName;
	}
	
	public int getSourceDCId() {
		return this.sourceDCId;
	}
	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}
	
	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}
	
	public void setDestId(String destId) {
		this.destId = destId;
	}
	
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	public void setOrderList(List<DsOrder> orderList) {
		this.orderList = orderList;
	}
	
	public void setOrderSum(int orderSum) {
		this.orderSum = orderSum;
	}
	
	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	/**
	 * @param sendTime
	 *            the sendTime to set
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime == null ? null : (Date) sendTime.clone();
	}
	
	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public void setSourceDCId(int sourceDCId) {
		this.sourceDCId = sourceDCId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (!this.getClass().equals(obj.getClass())) {
			return false;
		}
		
		DsBox box = (DsBox) obj;
		if (this.batchId == null || this.boxId == null) {
			return this.batchId == box.batchId || this.boxId == box.boxId;
		} else {
			return this.batchId.equals(box.batchId) && this.boxId.equals(box.boxId);
		}
	}
	
	@Override
	public int hashCode() {
		return this.batchId.hashCode() + this.boxId.hashCode();
	}
}
