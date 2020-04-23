package com.jd.bluedragon.common.dto.queueManagement.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

public class PdaPlatformReq implements Serializable {
    private static final long serialVersionUID = -1L;

    /**
     * 用户
     */
    private User user;

    /**
     * 站点
     */
    private CurrentOperate currentOperate;

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


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

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

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
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
