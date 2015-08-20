package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

public class LoadBillRequest implements Serializable {

	private static final long serialVersionUID = -1149135976182880663L;

	private String ids;

	private String sendCode;

	private Integer dmsCode;

	private Integer approvalCode;

	private Date sendTimeFrom;

	private Date sendTimeTo;

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

	public Date getSendTimeFrom() {
		return sendTimeFrom;
	}

	public void setSendTimeFrom(Date sendTimeFrom) {
		this.sendTimeFrom = sendTimeFrom;
	}

	public Date getSendTimeTo() {
		return sendTimeTo;
	}

	public void setSendTimeTo(Date sendTimeTo) {
		this.sendTimeTo = sendTimeTo;
	}

}
