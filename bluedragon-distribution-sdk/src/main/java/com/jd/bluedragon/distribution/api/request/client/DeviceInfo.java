package com.jd.bluedragon.distribution.api.request.client;

import java.io.Serializable;

/**
 * 设备信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-15 21:13:56 周二
 */
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = -6393958427346486561L;

    /**
     * 当前运行环境
     */
    private String runningEnv;

    /**
     * 系统编码
     */
    private String systemCode;

    /**
     * 程序类型，安卓客户端，iOS客户端，winCE客户端，PC客户端等
     */
    private String programTypeId;

    /**
     * 程序类型，安卓客户端，iOS客户端，winCE客户端，PC客户端等
     */
    private String programType;

    /**
     * 设备型号，厂家的型号，东大，销邦，优博讯等等
     */
    private String deviceType;

    /**
     * 当前版本号
     */
    private String versionCode;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备序列号
     */
    private String deviceSn;

    public String getRunningEnv() {
        return runningEnv;
    }

    public void setRunningEnv(String runningEnv) {
        this.runningEnv = runningEnv;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getProgramTypeId() {
        return programTypeId;
    }

    public void setProgramTypeId(String programTypeId) {
        this.programTypeId = programTypeId;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "runningEnv='" + runningEnv + '\'' +
                ", systemCode='" + systemCode + '\'' +
                ", programType='" + programType + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", deviceCode='" + deviceCode + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                '}';
    }
}
