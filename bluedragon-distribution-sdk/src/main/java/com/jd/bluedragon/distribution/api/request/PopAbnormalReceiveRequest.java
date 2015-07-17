package com.jd.bluedragon.distribution.api.request;

import java.util.Date;

public class PopAbnormalReceiveRequest {
	
	String serialNo;
	String waybillCode;
	Integer mainType;
	Integer subType;
	String comment;
	Date operateTime;
	String attr1;
	String isEnd;
	
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public Integer getMainType() {
		return mainType;
	}
	public void setMainType(Integer mainType) {
		this.mainType = mainType;
	}
	public Integer getSubType() {
		return subType;
	}
	public void setSubType(Integer subType) {
		this.subType = subType;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}
	public String getAttr1() {
		return attr1;
	}
	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}
	public String getIsEnd() {
		return isEnd;
	}
	public void setIsEnd(String isEnd) {
		this.isEnd = isEnd;
	}

}
