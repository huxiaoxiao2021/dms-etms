package com.jd.bluedragon.distribution.packageToMq.domain;

import java.io.Serializable;

public class WaybillMqMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 消息ID
	 */
	private String statementId;
	
	/**
	 * 运单号
	 */
//	private String waybillCode;
	
	/**
	 * 业务对象（暂定各参数放到Map中）
	 */
	private PackList msgObj;

	public WaybillMqMsg(){
		msgObj = new PackList();
	}
	
//	public String getWaybillCode() {
//		return waybillCode;
//	}
//
//	public void setWaybillCode(String waybillCode) {
//		this.waybillCode = waybillCode;
//	}

	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	public PackList getMsgObj() {
		return msgObj;
	}

	public void setMsgObj(PackList msgObj) {
		this.msgObj = msgObj;
	}
	
	public void addPack(Pack pack){
		msgObj.addPack(pack);
	}
	
	public void setWaybillCode(String waybillCode){
		msgObj.setWaybillCode(waybillCode);
	}
}
