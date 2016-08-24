package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

public class CrossBoxRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer originateOrg; // 始发区域ID
	private Integer destinationOrg; // 目的区域ID
	private Integer transferOrg; // 中转区域ID
	private String originalDmsName;// 始发分拣中心名称
	private String destinationDmsName;// 目的分拣中心名称
	private String transferName;// 中转站名称
	private String creator;
	private String updateOperatorName;// 数据维护人
	private Date createDate;// 数据维护时间
	private Date updateDate;// 数据维护时间
	private String createDateString;
	private String updateDateString;
	private String startDate;
	private String endDate;
	private Integer startIndex;
	private Integer endIndex;
	private Integer yn;

	public String getOriginalDmsName() {
		return originalDmsName;
	}

	public void setOriginalDmsName(String originalDmsName) {
		this.originalDmsName = originalDmsName;
	}

	public String getDestinationDmsName() {
		return destinationDmsName;
	}

	public void setDestinationDmsName(String destinationDmsName) {
		this.destinationDmsName = destinationDmsName;
	}

	public String getTransferName() {
		return transferName;
	}

	public void setTransferName(String transferName) {
		this.transferName = transferName;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public String getUpdateDateString() {
		return updateDateString;
	}

	public void setUpdateDateString(String updateDateString) {
		this.updateDateString = updateDateString;
	}

	public Integer getOriginateOrg() {
		return originateOrg;
	}

	public void setOriginateOrg(Integer originateOrg) {
		this.originateOrg = originateOrg;
	}

	public Integer getDestinationOrg() {
		return destinationOrg;
	}

	public void setDestinationOrg(Integer destinationOrg) {
		this.destinationOrg = destinationOrg;
	}

	public Integer getTransferOrg() {
		return transferOrg;
	}

	public void setTransferOrg(Integer transferOrg) {
		this.transferOrg = transferOrg;
	}

	public String getUpdateOperatorName() {
		return updateOperatorName;
	}

	public void setUpdateOperatorName(String updateOperatorName) {
		this.updateOperatorName = updateOperatorName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

}
