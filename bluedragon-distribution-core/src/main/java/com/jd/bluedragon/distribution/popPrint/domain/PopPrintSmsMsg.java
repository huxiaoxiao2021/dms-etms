package com.jd.bluedragon.distribution.popPrint.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: PopPrintSmsMsg
 * @Description: 推送给sms的消息体
 * @author: wuyoude
 * @date: 2017年7月17日 下午5:57:15
 *
 */
public class PopPrintSmsMsg implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 运单号
	 */
	private String waybillCode;
	/**
	 * 包裹号
	 */
    private String packageBarcode;
	/**
	 * 箱号
	 */
	private String boxCode;
	/**
	 * 创建站点编号
	 */
	private Integer createSiteCode;
	
	/**
	 * 创建站点名称
	 */
	private String createSiteName;
	
	/**
	 * 打印包裹人编号
	 */
	private Integer printPackCode;
	
	/**
	 * 打印包裹人名称
	 */
	private String printPackUser;
	
	/**
	 * 打印包裹时间
	 */
	private Date printPackTime;

	/**
	 * @return the waybillCode
	 */
	public String getWaybillCode() {
		return waybillCode;
	}

	/**
	 * @param waybillCode the waybillCode to set
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 * @return the createSiteCode
	 */
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	/**
	 * @param createSiteCode the createSiteCode to set
	 */
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	/**
	 * @return the createSiteName
	 */
	public String getCreateSiteName() {
		return createSiteName;
	}

	/**
	 * @param createSiteName the createSiteName to set
	 */
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}

	/**
	 * @return the printPackCode
	 */
	public Integer getPrintPackCode() {
		return printPackCode;
	}

	/**
	 * @param printPackCode the printPackCode to set
	 */
	public void setPrintPackCode(Integer printPackCode) {
		this.printPackCode = printPackCode;
	}

	/**
	 * @return the printPackUser
	 */
	public String getPrintPackUser() {
		return printPackUser;
	}

	/**
	 * @param printPackUser the printPackUser to set
	 */
	public void setPrintPackUser(String printPackUser) {
		this.printPackUser = printPackUser;
	}

	/**
	 * @return the printPackTime
	 */
	public Date getPrintPackTime() {
		return printPackTime;
	}

	/**
	 * @param printPackTime the printPackTime to set
	 */
	public void setPrintPackTime(Date printPackTime) {
		this.printPackTime = printPackTime;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	/**
	 * @return the packageBarcode
	 */
	public String getPackageBarcode() {
		return packageBarcode;
	}

	/**
	 * @param packageBarcode the packageBarcode to set
	 */
	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}
	
}
