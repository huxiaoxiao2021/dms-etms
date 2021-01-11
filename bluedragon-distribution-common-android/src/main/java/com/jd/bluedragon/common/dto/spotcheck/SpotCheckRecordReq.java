package com.jd.bluedragon.common.dto.spotcheck;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2021-01-11 16:08
 */
public class SpotCheckRecordReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String waybillCode;

    private Integer createSiteCode;

    public SpotCheckRecordReq() {
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
}
