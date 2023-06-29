package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;

/**
 * 客户端自动刷新配置
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-05-14 18:56:31 周日
 */
public class ClientAutoRefreshConfig implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 刷新间隔
     */
    private Integer refreshInterval;

    /**
     * 刷新提示
     */
    private String refreshTips;

    /**
     * 是否显示刷新提示
     */
    private Boolean showRefreshTips;

    public ClientAutoRefreshConfig() {
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Integer refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getRefreshTips() {
        return refreshTips;
    }

    public void setRefreshTips(String refreshTips) {
        this.refreshTips = refreshTips;
    }

    public Boolean getShowRefreshTips() {
        return showRefreshTips;
    }

    public void setShowRefreshTips(Boolean showRefreshTips) {
        this.showRefreshTips = showRefreshTips;
    }
}
