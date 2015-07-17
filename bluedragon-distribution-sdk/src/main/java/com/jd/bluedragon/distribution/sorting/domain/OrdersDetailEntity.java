package com.jd.bluedragon.distribution.sorting.domain;

import java.io.Serializable;

public class OrdersDetailEntity implements Serializable{
	
	private static final long serialVersionUID = 4831337335421636691L;

	/** 包裹号 */
	private String packNo;
	
	/** 运单号 */
	private String waybill;
	
	/** 箱号 */
	private String boxCode;
	
	/** 分拣站点编号 */
	private String siteCode;
	
	/** 分拣人账号 */
	private String userName;
	
	/** 分拣时间 */
	private String sortingTime;

	public String getPackNo() {
		return packNo;
	}

	public void setPackNo(String packNo) {
		this.packNo = packNo;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSortingTime() {
		return sortingTime;
	}

	public void setSortingTime(String sortingTime) {
		this.sortingTime = sortingTime;
	}
}
