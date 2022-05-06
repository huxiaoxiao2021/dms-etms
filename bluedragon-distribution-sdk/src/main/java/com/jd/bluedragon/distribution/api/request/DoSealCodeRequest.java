package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

/**
 * 无货上封签请求
 * @author wuyoude
 *
 */
public class DoSealCodeRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 解封签号列表
     */
    private List<String> sealCodes;

    /**
     * 解封签站点ID
     */
    private Integer sealSiteId;

    /**
     * 解封签站点名称
     */
    private String sealSiteName;

    /**
     * 解封签用户ERP
     */
    private String sealUserCode;

    /**
     * 解封签用户名
     */
    private String sealUserName;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
    }

	public Integer getSealSiteId() {
		return sealSiteId;
	}

	public void setSealSiteId(Integer sealSiteId) {
		this.sealSiteId = sealSiteId;
	}

	public String getSealSiteName() {
		return sealSiteName;
	}

	public void setSealSiteName(String sealSiteName) {
		this.sealSiteName = sealSiteName;
	}

	public String getSealUserCode() {
		return sealUserCode;
	}

	public void setSealUserCode(String sealUserCode) {
		this.sealUserCode = sealUserCode;
	}

	public String getSealUserName() {
		return sealUserName;
	}

	public void setSealUserName(String sealUserName) {
		this.sealUserName = sealUserName;
	}

}
