package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.enums.ScheduleAfterTypeEnum;

public class ReassignWaybillRequest extends JdRequest {
	private static final long serialVersionUID = -7910034488418807075L;
	
	/*包裹号*/
    private String packageBarcode;
    
	/*运单地址*/
    private String address;
    
    /* 预分拣目的站点编号 */
    private Integer receiveSiteCode;
    
	/* 预分拣目的站点名称 */
    private String receiveSiteName;
    
    /* 现场调度站点编号 */
    private Integer changeSiteCode;
    
    /* 现场调度站点名称 */
    private String changeSiteName;

	/*打印入口*/
	private Integer interfaceType;
	
	private List<String>  packageCodeList;
	
	private String waybillCode;
	
	private Integer scheduleAfterType = ScheduleAfterTypeEnum.PACKAGE_CODE.getCode();

	public String getPackageBarcode() {
  		return packageBarcode;
  	}

  	public void setPackageBarcode(String packageBarcode) {
  		this.packageBarcode = packageBarcode;
  	}
   
  	 public String getAddress() {
 		return address;
 	}

 	public void setAddress(String address) {
 		this.address = address;
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

 	public Integer getChangeSiteCode() {
 		return changeSiteCode;
 	}

 	public void setChangeSiteCode(Integer changeSiteCode) {
 		this.changeSiteCode = changeSiteCode;
 	}

 	public String getChangeSiteName() {
 		return changeSiteName;
 	}

 	public void setChangeSiteName(String changeSiteName) {
 		this.changeSiteName = changeSiteName;
 	}


	public Integer getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(Integer interfaceType) {
		this.interfaceType = interfaceType;
	}

	public List<String> getPackageCodeList() {
		return packageCodeList;
	}

	public void setPackageCodeList(List<String> packageCodeList) {
		this.packageCodeList = packageCodeList;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getScheduleAfterType() {
		return scheduleAfterType;
	}

	public void setScheduleAfterType(Integer scheduleAfterType) {
		this.scheduleAfterType = scheduleAfterType;
	}
}
