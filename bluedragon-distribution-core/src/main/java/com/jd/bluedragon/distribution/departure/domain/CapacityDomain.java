package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;

public class CapacityDomain implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2119111431295593354L;

	/**
	 * 始发区域
	 * */
	private String sorgid;

    /**
     * 始发区域名称
     */
    private String sorgName;
	
	/**
	 * 始发站
	 * */
	private String scode;

    /**
     * 始发站名称
     */
    private String sname;
	/**
	 * 目的区域
	 * */
	private String rorgid;

    /**
     * 目的区域名称
     */
    private String rorgName;
	
	/**
	 * 目的站
	 * */
	private String rcode;

    /**
     * 目的站名称
     */
    private String rname;
	
	/**
	 * 线路类型
	 * */
	private String routeType;
	
	/**
	 * 运力类型
	 * */
	private String tranType;
	
	/**
	 * 运输方式
	 * */
	private String tranMode;
	
	/**
	 * 运力编码
	 * */
	private String tranCode;
	
	/**
	 * 发车时间
	 * */
	private String sendTime;
	/**
	 * 发车时间HH:MM
	 * */
	private String sendTimeStr;

	/**
	 * 到车时间
	 * */
	private String arriveTime;
	
	/**
	 * 在途时长
	 * */
	private String travelTime;
	
	/**
	 * 承运商名称
	 * */
	private String carrierName;
	
	/**
	 * 承运商ID
	 * */
	private String carrierId;
	
	/**
	 * 航空班次
	 */
	private String airShiftName;

	/**
	 * 运力生效时间
	 */
	private String transEnableTime;

	/**
	 * 运力失效时间
	 */
	private String transDisableTime;

	/**
	 * 运力状态 (1:生效,2:失效)
	 */
	private Integer effectiveStatus;


	public String getSorgid() {
		return sorgid;
	}

	public void setSorgid(String sorgid) {
		this.sorgid = sorgid;
	}

	public String getScode() {
		return scode;
	}

	public void setScode(String scode) {
		this.scode = scode;
	}

	public String getRorgid() {
		return rorgid;
	}

	public void setRorgid(String rorgid) {
		this.rorgid = rorgid;
	}

	public String getRcode() {
		return rcode;
	}

	public void setRcode(String rcode) {
		this.rcode = rcode;
	}

	public String getRouteType() {
		return routeType;
	}

	public void setRouteType(String routeType) {
		this.routeType = routeType;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public String getTranMode() {
		return tranMode;
	}

	public void setTranMode(String tranMode) {
		this.tranMode = tranMode;
	}

	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getSendTimeStr() {
		return sendTimeStr;
	}

	public void setSendTimeStr(String sendTimeStr) {
		this.sendTimeStr = sendTimeStr;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getTravelTime() {
		return travelTime;
	}

	public void setTravelTime(String travelTime) {
		this.travelTime = travelTime;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}

	public String getAirShiftName() {
		return airShiftName;
	}

	public void setAirShiftName(String airShiftName) {
		this.airShiftName = airShiftName;
	}

    public String getSorgName() {
        return sorgName;
    }

    public void setSorgName(String sorgName) {
        this.sorgName = sorgName;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getRorgName() {
        return rorgName;
    }

    public void setRorgName(String rorgName) {
        this.rorgName = rorgName;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

	public String getTransEnableTime() {
		return transEnableTime;
	}

	public void setTransEnableTime(String transEnableTime) {
		this.transEnableTime = transEnableTime;
	}

	public String getTransDisableTime() {
		return transDisableTime;
	}

	public void setTransDisableTime(String transDisableTime) {
		this.transDisableTime = transDisableTime;
	}

	public Integer getEffectiveStatus() {
		return effectiveStatus;
	}

	public void setEffectiveStatus(Integer effectiveStatus) {
		this.effectiveStatus = effectiveStatus;
	}
}
