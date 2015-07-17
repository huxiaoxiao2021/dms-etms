package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

public class DepartureSendRequest extends JdRequest  {

	private static final long serialVersionUID = 8597863889220063166L;

	/** 批次号 */
	private String sendCode;
	
	/** 运单号(第三方) */
	private String thirdWaybillCode;

	/** 车号 */
	private String carCode; // 可以为空

	/** 发货司机 */
	private String sendUser;

	/** 发货司机编码 */
	private String sendUserCode;
	
	private Long sendCarSealsID;
	
	private Integer receiveSiteCode;
	
	/** 发车类型  0 传站发车  1支线发车   */
	private int type;
	
	/** 运力编码 */
	private String capacityCode;

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getSendUserCode() {
		return sendUserCode;
	}

	public void setSendUserCode(String sendUserCode) {
		this.sendUserCode = sendUserCode;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getSendCarSealsID() {
		return sendCarSealsID;
	}

	public void setSendCarSealsID(Long sendCarSealsID) {
		this.sendCarSealsID = sendCarSealsID;
	}

	public String getCapacityCode() {
		return capacityCode;
	}

	public void setCapacityCode(String capacityCode) {
		this.capacityCode = capacityCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	@Override
	public String toString() {
		return "DepartureSendRequest [sendCode=" + sendCode + ", carCode="
				+ carCode + ", sendUser=" + sendUser + ", sendUserCode="
				+ sendUserCode + ", sendCarSealsID=" + sendCarSealsID
				+ ", type=" + type + "]";
	}
	
}
