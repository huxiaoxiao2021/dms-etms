package com.jd.bluedragon.distribution.base.domain;

/**
 * @author yangpeng
 * PDA登录返回信息
 */
public class PdaStaff {	
	
	/**
	 * 错误信息
	 */
	private String errormsg;

	/**
	 * 调用结果
	 * isError = true：表示验证失败，有错误信息（errormsg）
	 * isError = false：表示验证成功，（staffId，staffName，siteId，siteName）不等于空
	 */
	private boolean isError;
	/**
	 * 用户ID
	 */
	private java.lang.Integer staffId;
	/**
	 * 用户名称
	 */
	private java.lang.String staffName;	
	/**
	 * 分拣中心ID
	 */
	private java.lang.Integer siteId;
	/**
	 * 分拣中心名称
	 */
	private java.lang.String siteName;
	/**
	 * 站点DMS编码
	 */
	private java.lang.String dmsCod;
	
	private Integer organizationId;//机构号
	private String organizationName;//机构名称
	private Integer siteType;   //站点类型
	private Integer subType;    //站点子类型
	public Integer getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	/**
	 * @return Integer 用户ID
	 */
	public java.lang.Integer getStaffId() {
		return staffId;
	}
	public void setStaffId(java.lang.Integer staffId) {
		this.staffId = staffId;
	}
	
	/**
	 * @return String 用户名称
	 */
	public java.lang.String getStaffName() {
		return staffName;
	}
	public void setStaffName(java.lang.String staffName) {
		this.staffName = staffName;
	}
	
	/**
	 * @return Integer 站点ID（分拣中心ID）
	 */
	public java.lang.Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(java.lang.Integer siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return String 站点名称（分拣中心名称）
	 */
	public java.lang.String getSiteName() {
		return siteName;
	}
	public void setSiteName(java.lang.String siteName) {
		this.siteName = siteName;
	}
	/**
	 * @return String 返回错误信息
	 */
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	/**
	 * @return boolean 是否验证失败
	 */
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public java.lang.String getDmsCod() {
		return dmsCod;
	}
	public void setDmsCod(java.lang.String dmsCod) {
		this.dmsCod = dmsCod;
	}

	public Integer getSiteType() {
		return siteType;
	}

	public void setSiteType(Integer siteType) {
		this.siteType = siteType;
	}

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}
}
