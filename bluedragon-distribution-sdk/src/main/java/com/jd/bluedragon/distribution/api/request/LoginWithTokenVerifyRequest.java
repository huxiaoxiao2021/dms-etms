package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * 登录的token验证request对象
 * @author fanggang7
 */
public class LoginWithTokenVerifyRequest implements Serializable {

    private String deviceId;

    private String token;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginWithTokenVerifyRequest{" +
                "deviceId='" + deviceId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
