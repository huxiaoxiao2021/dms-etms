package com.jd.bluedragon.distribution.coldchain.dto;

public class ColdChainQueryUnloadTaskRequest {
    /**
     * 分拣中心ID
     */
    private String siteId;

    /**
     * 查询时长（7天）
     */
    private String queryDays;

    /**
     * 状态（未完成 0、已完成 1、全部 2 ）
     */
    private int state;

    /**
     * 车牌号
     */
    private String vehicleNo;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getQueryDays() {
        return queryDays;
    }

    public void setQueryDays(String queryDays) {
        this.queryDays = queryDays;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
