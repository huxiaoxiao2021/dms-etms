package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

public class LoadBillRequest implements Serializable {

	private static final long serialVersionUID = -1149135976182880663L;

	private String ids;

	/** 批次号 , 可以传多个 , 逗号隔开   */
	private String sendCode;

	private Integer dmsCode;

	private Integer approvalCode;

	private String sendTimeFrom;

	private String sendTimeTo;

	private String waybillOrPackageCode;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Integer getDmsCode() {
		return dmsCode;
	}

	public void setDmsCode(Integer dmsCode) {
		this.dmsCode = dmsCode;
	}

	public Integer getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(Integer approvalCode) {
		this.approvalCode = approvalCode;
	}

	public String getSendTimeFrom() {
		return sendTimeFrom;
	}

	public void setSendTimeFrom(String sendTimeFrom) {
		this.sendTimeFrom = sendTimeFrom;
	}

	public String getSendTimeTo() {
		return sendTimeTo;
	}

	public void setSendTimeTo(String sendTimeTo) {
		this.sendTimeTo = sendTimeTo;
	}

	public String getWaybillOrPackageCode() {
		return waybillOrPackageCode;
	}

	public void setWaybillOrPackageCode(String waybillOrPackageCode) {
		this.waybillOrPackageCode = waybillOrPackageCode;
	}
}
