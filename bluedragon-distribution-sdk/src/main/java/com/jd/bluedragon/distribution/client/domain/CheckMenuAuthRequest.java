package com.jd.bluedragon.distribution.client.domain;

import java.io.Serializable;

/**
 * 
 * @author wuyoude
 *
 */
public class CheckMenuAuthRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 系统编码
	 */
	private String systemCode;
	/**
	 * 菜单编码
	 */
	private String menuCode;
	/**
	 * 用户编码/erp
	 */
	private String userCode;
	/**
	 * 用户所属站点
	 */
	private Integer siteCode;
	/**
	 * 用户所属站点-类型
	 */
	private Integer siteType;	
	/**
	 * 用户所属站点-子类型
	 */
	private Integer siteSubType;
	
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public Integer getSiteType() {
		return siteType;
	}
	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}
	public Integer getSiteSubType() {
		return siteSubType;
	}
	public void setSiteSubType(Integer siteSubType) {
		this.siteSubType = siteSubType;
	}
}
