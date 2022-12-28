package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationInfo;

/**
 * 登录request对象
 * Created by shipeilin on 2018/1/16.
 */
public class LoginRequest extends BaseRequest{
    private static final long serialVersionUID = 3477068159073538416L;

    /** 主机名称 */
    private String clientInfo;

    /**
     * 登录接口的版本，1：为新版本
     */
    private Byte loginVersion;
    /**
     * 校验版本
     */
    private Boolean checkVersion = Boolean.TRUE;
    /**
     * 基础版本号
     */
    private String baseVersionCode = "01-20010101";

    /**
     * 设备信息
     */
    private DeviceInfo deviceInfo;

    /**
     * 设备位置信息
     */
    private DeviceLocationInfo deviceLocationInfo;
    /**
     * 岗位信息
     */
    private String positionCode;
    
    public String getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }

    public Byte getLoginVersion() {
        return loginVersion;
    }

    public void setLoginVersion(Byte loginVersion) {
        this.loginVersion = loginVersion;
    }

	public Boolean getCheckVersion() {
		return checkVersion;
	}

	public void setCheckVersion(Boolean checkVersion) {
		this.checkVersion = checkVersion;
	}

	public String getBaseVersionCode() {
		return baseVersionCode;
	}

	public void setBaseVersionCode(String baseVersionCode) {
		this.baseVersionCode = baseVersionCode;
	}

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public DeviceLocationInfo getDeviceLocationInfo() {
        return deviceLocationInfo;
    }

    public void setDeviceLocationInfo(DeviceLocationInfo deviceLocationInfo) {
        this.deviceLocationInfo = deviceLocationInfo;
    }

	public String getPositionCode() {
		return positionCode;
	}

	public void setPositionCode(String positionCode) {
		this.positionCode = positionCode;
	}
}
