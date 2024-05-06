package com.jd.bluedragon.common.dto.waybill.request;

import java.io.Serializable;

public class WayBillRouterReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前站点
     */
    private Integer siteCode;

    /**
     * 运单号
     */
    private String waybillCode;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
