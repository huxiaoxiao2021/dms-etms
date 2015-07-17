package com.jd.bluedragon.distribution.version.domain;

public class VersionEntity {

	/** 分拣中心编号 */
	private String siteCode;
	/** 应用程序类型：11-卡西欧PDA,12-方正PDA, 20-Winform客户端*/
	private Integer programType;
	/** 版本号 */
	private String versionCode;
	/** 下载地址*/
	private String downloadUrl;
	
	public VersionEntity() {
	}

	public VersionEntity(String siteCode, Integer programType) {
		this.siteCode=siteCode;
		this.programType=programType;
	}

	public String getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public Integer getProgramType() {
		return programType;
	}

	public void setProgramType(Integer programType) {
		this.programType = programType;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
}
