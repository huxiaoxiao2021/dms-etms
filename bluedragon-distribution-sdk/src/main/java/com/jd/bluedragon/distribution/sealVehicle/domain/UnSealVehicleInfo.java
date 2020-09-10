package com.jd.bluedragon.distribution.sealVehicle.domain;


import java.io.Serializable;
import java.util.List;

public class UnSealVehicleInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	* 运力编码
	* */
	private String transportCode;

	 /*
	 * 始发站点编号
	 * */
	private Integer createSiteCode;

	 /*
	 * 始发站点名称
	 * */
	private String createSiteName;

	 /*
	 * 目的站点编号
	 * */
	private Integer receiveSiteCode;

	 /*
	 * 目的站点名称
	 * */
	private String receiveSiteName;

	 /*
	 * 运力最晚发车时间
	 * */
	private String sendCarTime;

	/*
	* 封车来源
	* */
	private Integer preSealSource;

	/*
	* 运输类型
	* */
	private Integer transWay;

	/*
	* 运输类型名称
	* */
	private String transWayName;

	 /*
	 * 系统标识
	 * */
	private String source;

	/*
	* 批次数量
	* */
	private Integer sendCodeCount;

	/*
	* 封车信息是否就绪
	* */
	private Boolean isReady;

    /**
     * 预封车录入人员
     */
	private String createUserErp;

	public String getTransportCode() {
		return transportCode;
	}

	public void setTransportCode(String transportCode) {
		this.transportCode = transportCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getCreateSiteName() {
		return createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public String getSendCarTime() {
		return sendCarTime;
	}

	public void setSendCarTime(String sendCarTime) {
		this.sendCarTime = sendCarTime;
	}

	public Integer getPreSealSource() {
		return preSealSource;
	}

	public void setPreSealSource(Integer preSealSource) {
		this.preSealSource = preSealSource;
	}

	public Integer getTransWay() {
		return transWay;
	}

	public void setTransWay(Integer transWay) {
		this.transWay = transWay;
	}

	public String getTransWayName() {
		return transWayName;
	}

	public void setTransWayName(String transWayName) {
		this.transWayName = transWayName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getSendCodeCount() {
		return sendCodeCount;
	}

	public void setSendCodeCount(Integer sendCodeCount) {
		this.sendCodeCount = sendCodeCount;
	}

	public Boolean getReady() {
		return isReady;
	}

	public void setReady(Boolean ready) {
		isReady = ready;
	}

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }
}
