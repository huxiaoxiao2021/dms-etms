package com.jd.bluedragon.distribution.newseal.domain;

import com.jd.bluedragon.Constants;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class CancelPreSealVehicleRequest {

	private static final long serialVersionUID = 1L;

	/** 车牌号 */
	private String vehicleNumber;

	/** 操作人编号 */
	private Integer siteCode;

	/** 操作人编号 */
	private String siteName;

	/** 操作人编号 */
	private String operateUserErp;

	/** 操作人姓名 */
	private String operateUserName;

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
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

	public String getOperateUserErp() {
		return operateUserErp;
	}

	public void setOperateUserErp(String operateUserErp) {
		this.operateUserErp = operateUserErp;
	}

	public String getOperateUserName() {
		return operateUserName;
	}

	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}

}
