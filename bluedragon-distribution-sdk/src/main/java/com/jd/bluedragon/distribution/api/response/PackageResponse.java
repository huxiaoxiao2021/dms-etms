package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class PackageResponse extends JdResponse{

	private static final long serialVersionUID = -4737285975266143104L;

	/*运单号*/
	private String waybillCode;
	
	/*包裹号*/
	private String packageBarcode;
	
	/*验货异常类型(1:少验,2:多验)*/
    private Integer inspectionECType;
    
	/*箱号*/
	private String boxCode;
	
	/*接收站点code*/
	private Integer receiveSiteCode;
	
	/*创建单位*/
	private Integer createSiteCode;
	
	private String createTime;
	
	/*地址*/
	private String address;

	/*接收站点名称*/
	private String receiveSiteName;
	
	public PackageResponse() {
		super();
	}

	public PackageResponse(String waybillCode, String packageBarcode,
			String createTime) {
		super();
		this.waybillCode = waybillCode;
		this.packageBarcode = packageBarcode;
		this.createTime = createTime;
	}

	public PackageResponse(String waybillCode, String packageBarcode,
			String createTime, String address) {
		super();
		this.waybillCode = waybillCode;
		this.packageBarcode = packageBarcode;
		this.createTime = createTime;
		this.address = address;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getInspectionECType() {
		return inspectionECType;
	}

	public void setInspectionECType(Integer inspectionECType) {
		this.inspectionECType = inspectionECType;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

}
