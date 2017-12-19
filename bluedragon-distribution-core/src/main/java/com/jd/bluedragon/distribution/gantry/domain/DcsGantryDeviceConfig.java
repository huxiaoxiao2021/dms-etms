package com.jd.bluedragon.distribution.gantry.domain;


import java.util.Date;

/**
 * Created by yanghongqiang on 2016/3/11.
 * 龙门架编号，操作时间，返回操作类型，操作人ID、姓名、操作站点ID，名称、批次号（可选）等其它信息
 * "siteCode":2015,"siteName":"北京双树分拣中心","staffName":"杨宏强","orgId":6,"dmsCode":"010F005"
 */
public class DcsGantryDeviceConfig {

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
