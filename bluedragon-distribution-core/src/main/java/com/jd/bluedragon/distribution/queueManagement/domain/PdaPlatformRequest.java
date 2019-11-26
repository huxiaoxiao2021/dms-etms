package com.jd.bluedragon.distribution.queueManagement.domain;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 获取月台、流向、车型列表请求对象
 */
public class PdaPlatformRequest extends JdRequest {

    /**
     * 租户编码
     */
    private String tenantCode;

    /**
     * 园区编码
     */
    private String parkCode;

    /**
     * 工作区类型
     */
    private int resourceType;

    /**
     * 作业区编码
     */
    private String currentStationCode;

    /**
     * 月台编码
     */
    private String platformCode;


    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getParkCode() {
        return parkCode;
    }

    public void setParkCode(String parkCode) {
        this.parkCode = parkCode;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceTypeEnum) {
        this.resourceType = resourceTypeEnum;
    }

    public String getCurrentStationCode() {
        return currentStationCode;
    }

    public void setCurrentStationCode(String currentStationCode) {
        this.currentStationCode = currentStationCode;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }
}
