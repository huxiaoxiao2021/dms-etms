package com.jd.bluedragon.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询运输月台号和联系人的入参实体
 */
@Data
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
}
