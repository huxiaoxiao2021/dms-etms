package com.jd.bluedragon.utils;

public class SysConfig {

	private String ownSign = "BASE";

	private int pageSize = 100;

	private int queueNum = 6;
	
	private String passportUrl;
	
	private String appPlatform;
	
	private String passportToken;
	private String zitiType;
	private String recommendProDiyOne;//自提属性配置一级站点类型
	private String recommendProDiyTwo;//自提属性配置二级站点类型

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getOwnSign() {
		return ownSign;
	}

	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}

	public int getQueueNum() {
		return queueNum;
	}

	public void setQueueNum(int queueNum) {
		this.queueNum = queueNum;
	}

	public String getPassportUrl() {
		return passportUrl;
	}

	public void setPassportUrl(String passportUrl) {
		this.passportUrl = passportUrl;
	}

	public String getAppPlatform() {
		return appPlatform;
	}

	public void setAppPlatform(String appPlatform) {
		this.appPlatform = appPlatform;
	}

	public String getPassportToken() {
		return passportToken;
	}

	public void setPassportToken(String passportToken) {
		this.passportToken = passportToken;
	}

	public String getZitiType() {
		return zitiType;
	}

	public void setZitiType(String zitiType) {
		this.zitiType = zitiType;
	}

	public String getRecommendProDiyOne() {
		return recommendProDiyOne;
	}

	public void setRecommendProDiyOne(String recommendProDiyOne) {
		this.recommendProDiyOne = recommendProDiyOne;
	}

	public String getRecommendProDiyTwo() {
		return recommendProDiyTwo;
	}

	public void setRecommendProDiyTwo(String recommendProDiyTwo) {
		this.recommendProDiyTwo = recommendProDiyTwo;
	}	
}
