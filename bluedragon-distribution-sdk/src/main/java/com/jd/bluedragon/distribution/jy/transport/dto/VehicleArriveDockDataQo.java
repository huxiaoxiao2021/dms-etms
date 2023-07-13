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
public class VehicleArriveDockDataQo extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 7321555449562322027L;

    /**
     * 始发场地ID
     */
    private Integer startSiteId;

    /**
     * 月台号
     */
    private String dockCode;

    /**
     * 是否手动刷新
     */
    private Integer manualRefresh;

    public VehicleArriveDockDataQo() {}

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getDockCode() {
        return dockCode;
    }

    public void setDockCode(String dockCode) {
        this.dockCode = dockCode;
    }

    public Integer getManualRefresh() {
        return manualRefresh;
    }

    public void setManualRefresh(Integer manualRefresh) {
        this.manualRefresh = manualRefresh;
    }
}
