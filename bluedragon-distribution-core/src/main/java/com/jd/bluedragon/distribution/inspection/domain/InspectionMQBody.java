package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2016/2/22.
 */
public class InspectionMQBody implements Serializable {
    private static final long serialVersionUID = 0L;

    /**
     * 验货站点编号
     */
    private Integer inspectionSiteCode;

    /**
     * 运单号
     */
    private String waybillCode;


    public Integer getInspectionSiteCode() {
        return inspectionSiteCode;
    }

    public void setInspectionSiteCode(Integer inspectionSiteCode) {
        this.inspectionSiteCode = inspectionSiteCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
