package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdRequest;

public class TurnoverBoxRequest extends JdRequest {
	private static final long serialVersionUID = -1710022841107243989L;

	private String turnoverBoxCode;

	private Integer receiveSiteCode;

	private String receiveSiteName;

	private List<String> turnoverBoxCodes;

	public List<String> getTurnoverBoxCodes() {
		return turnoverBoxCodes;
	}

	public void setTurnoverBoxCodes(List<String> turnoverBoxCodes) {
		this.turnoverBoxCodes = turnoverBoxCodes;
	}

	public String getTurnoverBoxCode() {
		return turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	@Override
	public String toString() {
		return "TurnoverBoxRequest [userCode=" + this.getUserCode()
				+ ", userName=" + this.getUserName() + ", siteCode="
				+ this.getSiteCode() + ", siteName=" + this.getSiteName()
				+ ", businessType=" + this.getBusinessType()
				+ ", receiveSiteCode=" + this.receiveSiteCode
				+ ", operateTime=" + this.getOperateTime()
				+ ",turnoverBoxCode=" + this.turnoverBoxCode
				+ ",receiveSiteName=" + this.receiveSiteName
				+ ",turnoverBoxCodes=(" + (this.turnoverBoxCodes==null?"null":this.turnoverBoxCodes.toString())
				+ ")]";

	}
}
