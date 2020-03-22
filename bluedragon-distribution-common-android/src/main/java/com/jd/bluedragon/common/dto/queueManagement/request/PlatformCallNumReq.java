package com.jd.bluedragon.common.dto.queueManagement.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

public class PlatformCallNumReq implements Serializable {
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
     * 月台编码
     */
    private String platformCode;

    /**
     * 流向编码
     */
    private String flowCode;

    /**
     * 车型
     */
    private String carType;

    /**
     * 作业类型
     */
    private int platformWorkTypeEnum;

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

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public int getPlatformWorkTypeEnum() {
        return platformWorkTypeEnum;
    }

    public void setPlatformWorkTypeEnum(int platformWorkTypeEnum) {
        this.platformWorkTypeEnum = platformWorkTypeEnum;
    }
}
