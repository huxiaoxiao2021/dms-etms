package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * @author lixin39
 * @Description 冷链卸货任务查询条件
 * @ClassName ColdChainUnloadQueryResultDto
 * @date 2020/3/4
 */
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
     * 状态（0-全部，1-卸货中，2-卸货完成
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
