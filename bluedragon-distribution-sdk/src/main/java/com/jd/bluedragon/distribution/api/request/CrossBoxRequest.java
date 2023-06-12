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
	private Integer pageSize;
	private Integer endIndex;
	private Integer yn;
	private String originateOrgName;//始发区域名称
	private String destinationOrgName;//目的区域名称
	private String transferOrgName;//中转区域名称

	private String originateProvinceAgencyCode; // 始发省区编码
	private String originateProvinceAgencyName; // 始发省区名称
	private String originateAreaHubCode; // 始发枢纽编码
	private String originateAreaHubName; // 始发枢纽名称
	private String transferProvinceAgencyCode; // 中转省区编码
	private String transferProvinceAgencyName; // 中转省区名称
	private String transferAreaHubCode; // 中转枢纽编码
	private String transferAreaHubName; // 中转枢纽名称
	private String destinationProvinceAgencyCode; // 目的省区编码
	private String destinationProvinceAgencyName; // 目的省区名称
	private String destinationAreaHubCode; // 目的枢纽编码
	private String destinationAreaHubName; // 目的枢纽名称

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

	public String getOriginateOrgName() {
		return originateOrgName;
	}

	public void setOriginateOrgName(String originateOrgName) {
		this.originateOrgName = originateOrgName;
	}

	public String getDestinationOrgName() {
		return destinationOrgName;
	}

	public void setDestinationOrgName(String destinationOrgName) {
		this.destinationOrgName = destinationOrgName;
	}

	public String getTransferOrgName() {
		return transferOrgName;
	}

	public void setTransferOrgName(String transferOrgName) {
		this.transferOrgName = transferOrgName;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOriginateProvinceAgencyCode() {
		return originateProvinceAgencyCode;
	}

	public void setOriginateProvinceAgencyCode(String originateProvinceAgencyCode) {
		this.originateProvinceAgencyCode = originateProvinceAgencyCode;
	}

	public String getOriginateProvinceAgencyName() {
		return originateProvinceAgencyName;
	}

	public void setOriginateProvinceAgencyName(String originateProvinceAgencyName) {
		this.originateProvinceAgencyName = originateProvinceAgencyName;
	}

	public String getOriginateAreaHubCode() {
		return originateAreaHubCode;
	}

	public void setOriginateAreaHubCode(String originateAreaHubCode) {
		this.originateAreaHubCode = originateAreaHubCode;
	}

	public String getOriginateAreaHubName() {
		return originateAreaHubName;
	}

	public void setOriginateAreaHubName(String originateAreaHubName) {
		this.originateAreaHubName = originateAreaHubName;
	}

	public String getTransferProvinceAgencyCode() {
		return transferProvinceAgencyCode;
	}

	public void setTransferProvinceAgencyCode(String transferProvinceAgencyCode) {
		this.transferProvinceAgencyCode = transferProvinceAgencyCode;
	}

	public String getTransferProvinceAgencyName() {
		return transferProvinceAgencyName;
	}

	public void setTransferProvinceAgencyName(String transferProvinceAgencyName) {
		this.transferProvinceAgencyName = transferProvinceAgencyName;
	}

	public String getTransferAreaHubCode() {
		return transferAreaHubCode;
	}

	public void setTransferAreaHubCode(String transferAreaHubCode) {
		this.transferAreaHubCode = transferAreaHubCode;
	}

	public String getTransferAreaHubName() {
		return transferAreaHubName;
	}

	public void setTransferAreaHubName(String transferAreaHubName) {
		this.transferAreaHubName = transferAreaHubName;
	}

	public String getDestinationProvinceAgencyCode() {
		return destinationProvinceAgencyCode;
	}

	public void setDestinationProvinceAgencyCode(String destinationProvinceAgencyCode) {
		this.destinationProvinceAgencyCode = destinationProvinceAgencyCode;
	}

	public String getDestinationProvinceAgencyName() {
		return destinationProvinceAgencyName;
	}

	public void setDestinationProvinceAgencyName(String destinationProvinceAgencyName) {
		this.destinationProvinceAgencyName = destinationProvinceAgencyName;
	}

	public String getDestinationAreaHubCode() {
		return destinationAreaHubCode;
	}

	public void setDestinationAreaHubCode(String destinationAreaHubCode) {
		this.destinationAreaHubCode = destinationAreaHubCode;
	}

	public String getDestinationAreaHubName() {
		return destinationAreaHubName;
	}

	public void setDestinationAreaHubName(String destinationAreaHubName) {
		this.destinationAreaHubName = destinationAreaHubName;
	}
}
