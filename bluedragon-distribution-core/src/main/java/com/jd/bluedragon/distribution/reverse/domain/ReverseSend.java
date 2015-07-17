package com.jd.bluedragon.distribution.reverse.domain;

public class ReverseSend {
    
    //操作人编号
    private String operatorId;
    
    // 操作人
    private String operatorName;
    
    //包裹编号
    private String packageCode;
    
    //取件单号
	private String pickWareCode;
    
    //发车时间
    private String dispatchTime;
    
	//发货批次
    private String sendCode;

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getPickWareCode() {
		return pickWareCode;
	}

	public void setPickWareCode(String pickWareCode) {
		this.pickWareCode = pickWareCode;
	}

	public String getDispatchTime() {
		return dispatchTime;
	}

	public void setDispatchTime(String dispatchTime) {
		this.dispatchTime = dispatchTime;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	@Override
	public String toString() {
		return "ReverseSend [operatorId=" + operatorId + ", operatorName="
				+ operatorName + ", packageCode=" + packageCode
				+ ", pickWareCode=" + pickWareCode + ", dispatchTime="
				+ dispatchTime + ", sendCode=" + sendCode + "]";
	}


}
