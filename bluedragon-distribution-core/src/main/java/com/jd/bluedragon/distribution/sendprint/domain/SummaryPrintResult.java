package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;
import java.util.List;

public class SummaryPrintResult implements Serializable {

	private static final long serialVersionUID = 6924730647940585634L;

	/** 箱子详细信息 */
	List<SummaryPrintBoxEntity> details;

	/** 始发地 */
	private String sendSiteName;

	/** 目的地 */
	private String receiveSiteName;
	/** 路区号，取该批次下第一个运单的路区号 */
	private String roadCode;
	/** 批次号 */
	private String sendCode;

	/** 发货时间 */
	private String sendTime;

	/** 合计箱数 */
	private int totalBoxNum;

	/** 合计应发包裹总数 */
	private int totalpackageBarNum;

	public List<SummaryPrintBoxEntity> getDetails() {
		return details;
	}

	public void setDetails(List<SummaryPrintBoxEntity> details) {
		this.details = details;
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

	public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getTotalBoxNum() {
		return totalBoxNum;
	}

	public void setTotalBoxNum(int totalBoxNum) {
		this.totalBoxNum = totalBoxNum;
	}

	public int getTotalpackageBarNum() {
		return totalpackageBarNum;
	}

	public void setTotalpackageBarNum(int totalpackageBarNum) {
		this.totalpackageBarNum = totalpackageBarNum;
	}

	/**
	 * @return the roadCode
	 */
	public String getRoadCode() {
		return roadCode;
	}

	/**
	 * @param roadCode the roadCode to set
	 */
	public void setRoadCode(String roadCode) {
		this.roadCode = roadCode;
	}
}
