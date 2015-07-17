package com.jd.bluedragon.distribution.departure.domain;

import java.io.Serializable;
import java.util.Date;

public class SendBox implements Serializable {
	
	private static final long serialVersionUID = 3249588433870945622L;

	/**  交接单号     */
	private String sendCode;
	
	/**  箱号      */
	private String boxCode;
	
	/**  运单号      */
	private String waybillCode;
	
	/**  包裹号      */
	private String packageBarcode;
	
	/**  司机      */
	private String sendUser;
	
	/**  发送时间    */
	private Date sendTime;
	
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
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public String getSendUser() {
		return sendUser;
	}
	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}
	public Date getSendTime() {
		return sendTime!=null?(Date)sendTime.clone():null;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime!=null?(Date)sendTime.clone():null;
	}
	public String getPackageBarcode() {
		return packageBarcode;
	}
	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}
	@Override
	public String toString() {
		return "SendBoxInfo [sendCode=" + sendCode + ", boxCode=" + boxCode
				+ ", waybillCode=" + waybillCode + ", sendUser=" + sendUser
				+ ", sendTime=" + sendTime + "]";
	}
	
}
