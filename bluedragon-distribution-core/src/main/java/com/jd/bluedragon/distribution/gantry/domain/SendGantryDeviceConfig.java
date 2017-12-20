package com.jd.bluedragon.distribution.gantry.domain;


import java.util.Date;

/**
 * Created by lihuachang on 2017/12/18.
 *
 */
public class SendGantryDeviceConfig {

private Date operateTime;
	
	private Integer receiveSiteCode;
	
	private GantryDeviceConfig config;

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public GantryDeviceConfig getConfig() {
		return config;
	}

	public void setConfig(GantryDeviceConfig config) {
		this.config = config;
	}
}
