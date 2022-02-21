package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import com.jd.bluedragon.common.dto.wastepackagestorage.request.ScanDiscardedPackagePo;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.io.Serializable;

/**
 * 弃件暂存上下文
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-06 11:06:18 周一
 */
public class DiscardedStorageContext implements Serializable {
    private static final long serialVersionUID = -8072320906792542351L;

    /**
     * 原始请求参数
     */
    private ScanDiscardedPackagePo scanDiscardedPackagePo;

    /**
     * 运单数据
     */
    private BigWaybillDto bigWaybillDto;

    /**
     * 场地信息
     */
    private BaseStaffSiteOrgDto currentSiteInfo;
    
    private String waybillCode;

    public DiscardedStorageContext() {
    }

    public ScanDiscardedPackagePo getScanDiscardedPackagePo() {
        return scanDiscardedPackagePo;
    }

    public DiscardedStorageContext setScanDiscardedPackagePo(ScanDiscardedPackagePo scanDiscardedPackagePo) {
        this.scanDiscardedPackagePo = scanDiscardedPackagePo;
        return this;
    }

    public BigWaybillDto getBigWaybillDto() {
        return bigWaybillDto;
    }

    public DiscardedStorageContext setBigWaybillDto(BigWaybillDto bigWaybillDto) {
        this.bigWaybillDto = bigWaybillDto;
        return this;
    }

    public BaseStaffSiteOrgDto getCurrentSiteInfo() {
        return currentSiteInfo;
    }

    public DiscardedStorageContext setCurrentSiteInfo(BaseStaffSiteOrgDto currentSiteInfo) {
        this.currentSiteInfo = currentSiteInfo;
        return this;
    }

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}
}
