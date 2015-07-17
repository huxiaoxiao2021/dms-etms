package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;


public class WayBillCodeRequest extends JdRequest{
	private static final long serialVersionUID = 8129768370214164981L;
	 /*包裹号*/
    private String packageBarcode;
	/*运单号*/
    private String waybillCode;
    /*三方code*/
	private Integer partnerSiteCode;
    
    
    
    public Integer getPartnerSiteCode() {
		return partnerSiteCode;
	}
	public void setPartnerSiteCode(Integer partnerSiteCode) {
		this.partnerSiteCode = partnerSiteCode;
	}
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
}
