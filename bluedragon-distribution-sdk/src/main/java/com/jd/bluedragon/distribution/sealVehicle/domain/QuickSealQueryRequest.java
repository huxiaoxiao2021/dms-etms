package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;

/**
 * @ClassName QuickSealQueryRequest
 * @Description
 * @Author wuyoude
 * @Date 2021/01/04 10:10
 **/
public class QuickSealQueryRequest implements Serializable {

    private static final long serialVersionUID = -8364364057624304094L;

    /**
     * 操作分拣中心
     */
    private Integer createSiteCode;

    /**
     * 查询时间范围
     */
    private Integer hourRange;

    /**
     * 预封车录入人
     */
    private String createUserErp;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 目的站点编码或名称
     */
    private String receiveSiteCodeOrName;
    /**
     * 发车时间-开始
     */
    private String sendCarStartTime;
    /**
     * 发车时间-结束
     */
    private String sendCarEndTime;
    
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public Integer getHourRange() {
		return hourRange;
	}
	public void setHourRange(Integer hourRange) {
		this.hourRange = hourRange;
	}
	public String getCreateUserErp() {
		return createUserErp;
	}
	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public String getReceiveSiteCodeOrName() {
		return receiveSiteCodeOrName;
	}
	public void setReceiveSiteCodeOrName(String receiveSiteCodeOrName) {
		this.receiveSiteCodeOrName = receiveSiteCodeOrName;
	}
	public String getSendCarStartTime() {
		return sendCarStartTime;
	}
	public void setSendCarStartTime(String sendCarStartTime) {
		this.sendCarStartTime = sendCarStartTime;
	}
	public String getSendCarEndTime() {
		return sendCarEndTime;
	}
	public void setSendCarEndTime(String sendCarEndTime) {
		this.sendCarEndTime = sendCarEndTime;
	}
}
