package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DetailPrintResult implements Serializable {

	private static final long serialVersionUID = 6924730647940585634L;

	/** 箱子详细信息 */
	List<DetailPrintPackageEntity> entities;

	/** 始发地 */
	private String sendSiteName;

	/** 目的地 */
	private String receiveSiteName;

	/** 批次号 */
	private String sendCode;

	/** 发货时间 */
	private Date sendTime;

	public List<DetailPrintPackageEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<DetailPrintPackageEntity> entities) {
		this.entities = entities;
	}

	public String getSendSiteName() {
		return sendSiteName;
	}

	public void setSendSiteName(String sendSiteName) {
		this.sendSiteName = sendSiteName;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public Date getSendTime() {
		return this.sendTime == null ? null : (Date) this.sendTime.clone();
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime == null ? null : (Date) sendTime.clone();
	}
	
}

