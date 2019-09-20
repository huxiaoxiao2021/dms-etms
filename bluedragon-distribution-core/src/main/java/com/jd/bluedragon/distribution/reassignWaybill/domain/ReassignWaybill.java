package com.jd.bluedragon.distribution.reassignWaybill.domain;

import java.util.Date;

import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;

public class ReassignWaybill {
	/* 包裹号 */
	private String packageBarcode;

	/* 运单地址 */
	private String address;
	
	/* 预分拣目的站点编号 */
	private Integer receiveSiteCode;
	
	/* 预分拣目的站点名称 */
	private String receiveSiteName;
	
	/* 现场调度站点编号 */
	private Integer changeSiteCode;
	
	/* 现场调度站点名称 */
	private String changeSiteName;
	
	/* PDA操作时间 */
	private Date operateTime;

	/* 操作人编号_ERP帐号 */
	private Integer userCode;

	/* 操作人姓名 */
	private String userName;

	/* 操作人所属站点编号 */
	private Integer siteCode;

	/* 操作人所属站点编号 */
	private String siteName;

    private String waybillCode;
	
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

	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}

	public Integer getUserCode() {
		return userCode;
	}

	public void setUserCode(Integer userCode) {
		this.userCode = userCode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
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
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public static ReassignWaybill toReassignWaybill(ReassignWaybillRequest request){
		ReassignWaybill packTagPrint=new ReassignWaybill();
		packTagPrint.setPackageBarcode(request.getPackageBarcode());
		packTagPrint.setAddress(request.getAddress());
		packTagPrint.setSiteCode(request.getSiteCode());
		packTagPrint.setSiteName(request.getSiteName());
		packTagPrint.setReceiveSiteCode(request.getReceiveSiteCode());
		packTagPrint.setReceiveSiteName(request.getReceiveSiteName());
		packTagPrint.setChangeSiteCode(request.getChangeSiteCode());
		packTagPrint.setChangeSiteName(request.getChangeSiteName());
		packTagPrint.setUserCode(request.getUserCode());
		packTagPrint.setUserName(request.getUserName());
		packTagPrint.setOperateTime(DateHelper.parseDateTime(request.getOperateTime()));
		packTagPrint.setWaybillCode(WaybillUtil.getWaybillCode(request.getPackageBarcode()));
		return packTagPrint;
	}
}
