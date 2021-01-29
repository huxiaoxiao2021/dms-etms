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
}
