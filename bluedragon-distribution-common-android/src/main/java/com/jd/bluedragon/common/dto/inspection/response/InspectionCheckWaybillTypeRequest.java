package com.jd.bluedragon.common.dto.inspection.response;

import java.io.Serializable;

/**
 * @description: 验货校验
 * @author: wuming
 * @create: 2021-03-06 14:33
 */
public class InspectionCheckWaybillTypeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String waybillCode;

    private Integer currentSiteCode;

    public InspectionCheckWaybillTypeRequest() {
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }
}
