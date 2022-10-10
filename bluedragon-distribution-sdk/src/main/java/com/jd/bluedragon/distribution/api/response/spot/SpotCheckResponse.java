package com.jd.bluedragon.distribution.api.response.spot;

import java.io.Serializable;

public class SpotCheckResponse implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 是否抽检
	 */
	private Boolean needCheck;
	/**
	 * 抽检类型
	 */
	private Integer spotCheckType;

	public Boolean getNeedCheck() {
		return needCheck;
	}

	public void setNeedCheck(Boolean needCheck) {
		this.needCheck = needCheck;
	}

	public Integer getSpotCheckType() {
		return spotCheckType;
	}

	public void setSpotCheckType(Integer spotCheckType) {
		this.spotCheckType = spotCheckType;
	}	
	
}
