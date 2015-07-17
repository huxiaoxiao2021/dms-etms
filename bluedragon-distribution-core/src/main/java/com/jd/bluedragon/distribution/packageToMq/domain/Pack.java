package com.jd.bluedragon.distribution.packageToMq.domain;

import java.io.Serializable;
import java.util.Date;

public class Pack implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String packWeight;
	String operatorName;
	String operatorId;
	String packVolume;
	String packCode;
	Date operatorTime;
	
	public String getPackWeight() {
		return packWeight;
	}
	public void setPackWeight(String packWeight) {
		this.packWeight = packWeight;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getPackVolume() {
		return packVolume;
	}
	public void setPackVolume(String packVolume) {
		this.packVolume = packVolume;
	}
	public String getPackCode() {
		return packCode;
	}
	public void setPackCode(String packCode) {
		this.packCode = packCode;
	}
	public Date getOperatorTime() {
		return operatorTime!=null?(Date)operatorTime.clone():null;
	}
	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime!=null?(Date)operatorTime.clone():null;
	}
}
