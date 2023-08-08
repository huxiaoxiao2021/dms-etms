package com.jd.bluedragon.distribution.workStation.domain;

import java.io.Serializable;

/**
 * 查询运输月台号和联系人的入参实体
 */

public class DockCodeAndPhoneQuery implements Serializable {

	private static final long serialVersionUID = -6518171737746340122L;

	/**
	 *发货地ID
	 */
	private String startSiteID;
	/**
	 *发货地名称
	 */
	private String startSiteName;
	/**
	 *目的地ID
	 */
	private String endSiteID;
	/**
	 *目的地名称
	 */
	private String endSiteName;
	/**
	 *类型（发货1、卸货2）
	 */
	private Integer SiteType;

	public String getStartSiteID() {
		return startSiteID;
	}

	public void setStartSiteID(String startSiteID) {
		this.startSiteID = startSiteID;
	}

	public String getStartSiteName() {
		return startSiteName;
	}

	public void setStartSiteName(String startSiteName) {
		this.startSiteName = startSiteName;
	}

	public String getEndSiteID() {
		return endSiteID;
	}

	public void setEndSiteID(String endSiteID) {
		this.endSiteID = endSiteID;
	}

	public String getEndSiteName() {
		return endSiteName;
	}

	public void setEndSiteName(String endSiteName) {
		this.endSiteName = endSiteName;
	}

	public Integer getSiteType() {
		return SiteType;
	}

	public void setSiteType(Integer siteType) {
		SiteType = siteType;
	}
}
