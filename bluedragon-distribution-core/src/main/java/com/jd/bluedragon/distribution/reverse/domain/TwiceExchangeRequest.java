package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
/**
 * 二次换单请求信息
 * @author wuyoude
 *
 */
public class TwiceExchangeRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 运单号
     */
    private String waybillCode ;
    /**
     * 分拣中心编码
     */
    private Integer dmsSiteCode;
    /**
     * 分拣中心名称
     */ 
    private String dmsSiteName;
    /**
     * 用户erp
     */
    private String userErp;
    
	public String getWaybillCode() {
		return waybillCode;
	}
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
	public Integer getDmsSiteCode() {
		return dmsSiteCode;
	}
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}
	public String getDmsSiteName() {
		return dmsSiteName;
	}
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}
	public String getUserErp() {
		return userErp;
	}
	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}
}
