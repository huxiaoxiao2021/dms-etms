package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;

public class TurnoverBoxInfo implements Serializable{
	
	private static final long serialVersionUID = -1678530886714584487L;

	//周转箱号
	private String turnoverBoxCode;
	
	//发送批次号
	private String sendCode;
		
	//箱号
	private String boxCode;
		
	//分拣中心编号
	private Integer operatorSortingId;
		
	//分拣中心名称
	private String operatorSortingName;
			
	//发货时间
	private String operateTime;
	
	//发送目的站点编号
	private Integer destSiteId;
	
	//发送目的站点名称
	private String destSiteName;
	
	//发货人编号
	private Integer operatorId;
		
	//发货人姓名
	private String operatorName;
		
	//消息类型
	private String messageType;
	
	//收货正逆向类型
	private String flowFlag;

	public String getFlowFlag() {
		return flowFlag;
	}

	public void setFlowFlag(String flowFlag) {
		this.flowFlag = flowFlag;
	}

	public String getTurnoverBoxCode() {
		return turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getOperatorSortingName() {
		return operatorSortingName;
	}

	public void setOperatorSortingName(String operatorSortingName) {
		this.operatorSortingName = operatorSortingName;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getDestSiteName() {
		return destSiteName;
	}

	public void setDestSiteName(String destSiteName) {
		this.destSiteName = destSiteName;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Integer getOperatorSortingId() {
		return operatorSortingId;
	}

	public void setOperatorSortingId(Integer operatorSortingId) {
		this.operatorSortingId = operatorSortingId;
	}

	public Integer getDestSiteId() {
		return destSiteId;
	}

	public void setDestSiteId(Integer destSiteId) {
		this.destSiteId = destSiteId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

}
