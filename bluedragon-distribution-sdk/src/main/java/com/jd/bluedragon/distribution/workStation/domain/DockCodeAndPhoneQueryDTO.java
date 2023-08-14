package com.jd.bluedragon.distribution.workStation.domain;

import java.io.Serializable;

/**
 * 查询运输月台号和联系人的入参实体
 */

public class DockCodeAndPhoneQueryDTO implements Serializable {

	private static final long serialVersionUID = -6518171737746340122L;

	/**
	 * 发货地ID
	 */
	private String startSiteID;
	/**
	 * 目的地ID
	 */
	private String endSiteID;
	/**
	 * 类型（发货1、卸货2）
	 */
	private Integer flowDirectionType;
	/**
	 * 月台号
	 */
	private Integer dockCode;

	public String getStartSiteID() {
		return startSiteID;
	}

	public void setStartSiteID(String startSiteID) {
		this.startSiteID = startSiteID;
	}

	public String getEndSiteID() {
		return endSiteID;
	}

	public void setEndSiteID(String endSiteID) {
		this.endSiteID = endSiteID;
	}

	public Integer getFlowDirectionType() {
		return flowDirectionType;
	}

	public void setFlowDirectionType(Integer flowDirectionType) {
		this.flowDirectionType = flowDirectionType;
	}

	public Integer getDockCode() {
		return dockCode;
	}

	public void setDockCode(Integer dockCode) {
		this.dockCode = dockCode;
	}
}


