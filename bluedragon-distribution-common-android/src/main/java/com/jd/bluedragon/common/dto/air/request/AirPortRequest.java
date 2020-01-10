package com.jd.bluedragon.common.dto.air.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 根据航班取始末机场 请求参数
 * @author : xumigen
 * @date : 2019/11/4
 */
public class AirPortRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private CurrentOperate currentOperate;
    private User user;

    /**
     * 航班号
     */
    private String flightNumber;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
