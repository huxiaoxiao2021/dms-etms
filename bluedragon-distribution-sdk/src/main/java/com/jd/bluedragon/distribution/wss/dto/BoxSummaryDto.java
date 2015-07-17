package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;

/**
 * @author zhuchao
 *
 */
public class BoxSummaryDto implements Serializable {
	
	private static final long serialVersionUID = -6643378407388822034L;

	/**  交接单号     */
	private String sendCode;
	
	/**  箱号      */
	private String boxCode;
	
	/**  运单数      */
	private Integer waybillNum;
	
	/**  包裹数     */
	private Integer packagebarNum;
	
	/**  司机      */
	private String sendUser;

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

	public Integer getWaybillNum() {
		return waybillNum;
	}

	public void setWaybillNum(Integer waybillNum) {
		this.waybillNum = waybillNum;
	}

	public Integer getPackagebarNum() {
		return packagebarNum;
	}

	public void setPackagebarNum(Integer packagebarNum) {
		this.packagebarNum = packagebarNum;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}
}
