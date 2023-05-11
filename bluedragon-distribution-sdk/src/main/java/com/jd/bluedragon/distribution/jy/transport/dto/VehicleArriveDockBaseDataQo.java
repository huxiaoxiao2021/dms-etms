package com.jd.bluedragon.distribution.jy.transport.dto;

import com.jd.bluedragon.distribution.api.request.base.BaseRequest;

import java.io.Serializable;

/**
 * 获取运输车辆靠台基础数据请求入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-09 10:40:34 周二
 */
public class VehicleArriveDockBaseDataQo extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 7321555449562322027L;

    /**
     * 始发场地编码
     */
    private String startSiteCode;

    /**
     * 始发场地ID
     */
    private Integer startSiteId;

    public VehicleArriveDockBaseDataQo() {}

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }
}
