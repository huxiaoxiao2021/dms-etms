package com.jd.bluedragon.distribution.receive.domain;

import java.util.Date;

public class TurnoverBox implements java.io.Serializable {

	private static final long serialVersionUID = -5342873771896641567L;

	private Long turnoverBoxId;

	private String turnoverBoxCode;

	private String createUser;

	private Integer createUserCode;

	private Date createTime;

	private Date updateTime;

	private Integer createSiteCode;

	private Integer yn;
	
	/** 1.收 2.发 3.取消 */
	private Integer operateType;

	private Date operateTime;

	private Integer receiveSiteCode;

	private String receiveSiteName;

	private String createSiteName;
	
	public String printStartTime;
	
	public String printEndTime;
	
	public Integer start;
	
	public Integer end;
	
	public Integer orgCode;

	public Integer getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	public String getReceiveSiteName() {
		return this.receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public String getCreateSiteName() {
		return this.createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public Long getTurnoverBoxId() {
		return this.turnoverBoxId;
	}

	public void setTurnoverBoxId(Long turnoverBoxId) {
		this.turnoverBoxId = turnoverBoxId;
	}

	public String getTurnoverBoxCode() {
		return this.turnoverBoxCode;
	}

	public void setTurnoverBoxCode(String turnoverBoxCode) {
		this.turnoverBoxCode = turnoverBoxCode;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Integer getCreateUserCode() {
		return this.createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getCreateSiteCode() {
		return this.createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getYn() {
		return this.yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

	public Integer getOperateType() {
		return this.operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Date getOperateTime() {
		return this.operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getReceiveSiteCode() {
		return this.receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getPrintStartTime() {
		return printStartTime;
	}

	public void setPrintStartTime(String printStartTime) {
		this.printStartTime = printStartTime;
	}

	public String getPrintEndTime() {
		return printEndTime;
	}

	public void setPrintEndTime(String printEndTime) {
		this.printEndTime = printEndTime;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}
	
}
