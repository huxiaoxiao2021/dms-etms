package com.jd.bluedragon.core.jsf.presort;

import java.io.Serializable;

public class AoiBindRoadMappingQuery implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String aoiId;
	private String siteCode;
	/**
	 * 路区编码
	 */
	private String roadNo;
	
	public String getAoiId() {
		return aoiId;
	}
	public void setAoiId(String aoiId) {
		this.aoiId = aoiId;
	}
	public String getSiteCode() {
		return siteCode;
	}
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	public String getRoadNo() {
		return roadNo;
	}
	public void setRoadNo(String roadNo) {
		this.roadNo = roadNo;
	}
	
}
