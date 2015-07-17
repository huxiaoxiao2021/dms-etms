package com.jd.bluedragon.distribution.api.request;

import java.util.Date;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 用于异常处理及异常查询时
 */
public class PackageRequest extends JdRequest {

	private static final long serialVersionUID = -3684248279087971142L;

	private String packageBarcode;
	private String waybillCode;
	private Date createTime;

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Date getCreateTime() {
		return this.createTime == null ? null : (Date) this.createTime.clone();
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime == null ? null : (Date) createTime.clone();
	}
}
