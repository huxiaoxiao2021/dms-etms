package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.NewSealVehicleBean;

import java.util.List;

/**
 * create by zhanglei 2017-05-10
 * 增加运力编码、批次、重量、体积及封车号的多层级关系
 */
public class NewSealVehicleRequest extends JdRequest {

	private static final long serialVersionUID = -4900034488418821323L;

	/** 车牌号 */
	private String vehicleCode;

	/** 交接单号 */
	private String handoverCode;

	/** 封车号 */
	private List<String> sealVehicleCodes;

	/**批次基本信息*/
	private List<NewSealVehicleBean> newSealVehicles;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 创建站点名称 */
	private String createSiteName;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getVehicleCode() {
		return vehicleCode;
	}

	public void setVehicleCode(String vehicleCode) {
		this.vehicleCode = vehicleCode;
	}

	public String getHandoverCode() {
		return handoverCode;
	}

	public void setHandoverCode(String handoverCode) {
		this.handoverCode = handoverCode;
	}

	public List<String> getSealVehicleCodes() {
		return sealVehicleCodes;
	}

	public void setSealVehicleCodes(List<String> sealVehicleCodes) {
		this.sealVehicleCodes = sealVehicleCodes;
	}

	public List<NewSealVehicleBean> getNewSealVehicles() {
		return newSealVehicles;
	}

	public void setNewSealVehicles(List<NewSealVehicleBean> newSealVehicles) {
		this.newSealVehicles = newSealVehicles;
	}

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
}
