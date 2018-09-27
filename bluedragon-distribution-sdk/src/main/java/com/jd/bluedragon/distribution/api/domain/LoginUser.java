package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;

public class LoginUser implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 当前登录用户ID
	 */
	private Integer userId;
	/**
	 * 当前登录用户ERP账号
	 */
	private String userErp;
	/**
	 * 当前登录用户名称
	 */
	private String userName;

	/**
	 * 青龙用户编号
	 */
	private Integer staffNo;
	/**
	 * 当前登录用户机构编号
	 */
	private Integer orgId;
	/**
	 * 当前登录用户机构名称
	 */
	private String orgName;
	/**
	 * 当前登录用户站点类型
	 */
	private Integer siteType;
	/**
	 * 当前登录用户站点编号
	 */
	private Integer siteCode;
	/**
	 * 当前登录用户站点名称
	 */
	private String siteName;

    /**
     * 当前登录用户站点7位编码
     */
    private String dmsSiteCode;

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	/**
	 * @return the userErp
	 */
	public String getUserErp() {
		return userErp;
	}
	/**
	 * @param userErp the userErp to set
	 */
	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the staffNo
	 */
	public Integer getStaffNo() {
		return staffNo;
	}
	/**
	 * @param staffNo the staffNo to set
	 */
	public void setStaffNo(Integer staffNo) {
		this.staffNo = staffNo;
	}
	/**
	 * @return the orgId
	 */
	public Integer getOrgId() {
		return orgId;
	}
	/**
	 * @param orgId the orgId to set
	 */
	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}
	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}
	/**
	 * @param orgName the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	/**
	 * @return the siteType
	 */
	public Integer getSiteType() {
		return siteType;
	}
	/**
	 * @param siteType the siteType to set
	 */
	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}
	/**
	 * @return the siteCode
	 */
	public Integer getSiteCode() {
		return siteCode;
	}
	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	/**
	 * @return the siteName
	 */
	public String getSiteName() {
		return siteName;
	}
	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

    public String getDmsSiteCode() {
        return dmsSiteCode;
    }

    public void setDmsSiteCode(String dmsSiteCode) {
        this.dmsSiteCode = dmsSiteCode;
    }
}
