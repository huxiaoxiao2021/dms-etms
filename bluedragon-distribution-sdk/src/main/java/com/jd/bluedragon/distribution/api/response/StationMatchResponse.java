package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

public class StationMatchResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 匹配站点编码
	 */
	private Integer siteCode;
	/**
	 * 匹配站点名称
	 */
	private String siteName;
	
	public Integer getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
