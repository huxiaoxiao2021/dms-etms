package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

public class CapacityCodeRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5856233991915540643L;

	/**
	 * 始发区域
	 * */
	private Integer sorgid;
	
	/**
	 * 始发站
	 * */
	private Integer scode;
	
	/**
	 * 目的区域
	 * */
	private Integer rorgid;
	
	/**
	 * 目的站
	 * */
	private Integer rcode;
	
	/**
	 * 线路类型
	 * */
	private Integer routeType;
	
	/**
	 * 运力类型
	 * */
	private Integer tranType;
	
	/**
	 * 运输方式
	 * */
	private Integer tranMode;
	
	/**
	 * 运力编码
	 * */
	private String tranCode;
	
	/**
	 * 承运商信息
	 * */
	private String carrierId;
	

	public Integer getSorgid() {
		return sorgid;
	}

	public void setSorgid(Integer sorgid) {
		this.sorgid = sorgid;
	}

	public Integer getScode() {
		return scode;
	}

	public void setScode(Integer scode) {
		this.scode = scode;
	}

	public Integer getRorgid() {
		return rorgid;
	}

	public void setRorgid(Integer rorgid) {
		this.rorgid = rorgid;
	}

	public Integer getRcode() {
		return rcode;
	}

	public void setRcode(Integer rcode) {
		this.rcode = rcode;
	}

	public Integer getRouteType() {
		return routeType;
	}

	public void setRouteType(Integer routeType) {
		this.routeType = routeType;
	}

	public Integer getTranType() {
		return tranType;
	}

	public void setTranType(Integer tranType) {
		this.tranType = tranType;
	}

	public Integer getTranMode() {
		return tranMode;
	}

	public void setTranMode(Integer tranMode) {
		this.tranMode = tranMode;
	}

	public String getTranCode() {
		return tranCode;
	}

	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}

	
}
