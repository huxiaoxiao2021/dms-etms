package com.jd.bluedragon.distribution.api.response;

import java.util.Date;

public class HandoverDetailResponse {

	/** 分拣中心ID */
	private Integer createSiteCode;

	/** 分拣中心名称 */
	private String createSiteName;

	/** 运单编号 */
	private String waybillCode;

	/** 包裹编号 */
	private String packageCode;

	/** 验货人编号 */
	private Integer inspectionUserCode;

	/** 验货人 */
	private String inspectionUser;

	/** 验货时间 */
	private Date inspectionTime;

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getCreateSiteName() {
		return createSiteName;
	}

	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public Integer getInspectionUserCode() {
		return inspectionUserCode;
	}

	public void setInspectionUserCode(Integer inspectionUserCode) {
		this.inspectionUserCode = inspectionUserCode;
	}

	public String getInspectionUser() {
		return inspectionUser;
	}

	public void setInspectionUser(String inspectionUser) {
		this.inspectionUser = inspectionUser;
	}

	public Date getInspectionTime() {
		return this.inspectionTime == null ? null : (Date) this.inspectionTime.clone();
	}

	public void setInspectionTime(Date inspectionTime) {
		this.inspectionTime = inspectionTime == null ? null : (Date) inspectionTime.clone();
	}

}
