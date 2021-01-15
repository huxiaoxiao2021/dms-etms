package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

public class EquipmentIdRequest implements Serializable {

    /**
     * 时间戳
     */
    private Long timeStamp;

    /**
     * 指纹程序版本号（v1.0.0）
     */
    private String fpVersion;

    /**
     * 应用程序包名
     */
    private String packageName;

    /**
     * 程序名
     */
    private String appName;

    /**
     * 程序版本号
     */
    private String appVersion;
    /**
     * 平台（windows/macOS）
     */
    private String platform ;

    /**
     * PC设备设置的名称 如 "xxx 的 PC”
     */
    private String deviceName ;

    /**
     * 系统版本号
     */
    private String systemVersion ;

    /**
     * 网卡mac地址，格式：[{"address":"FC-AA-14-50-F6-B0","type":"MIB_IF_TYPE_ETHERNET"}]
     */
    private String mac ;

    /**
     * 硬盘序列号
     */
    private String diskDriveSerial ;

    /**
     * BIOS序列号
     */
    private String BIOSSerial ;

    /**
     * 主板UUID
     */
    private String boardUUID ;

    /**
     * machineGUID: 注册表中 windows系统的GUID
     */
    private String machineGUID ;

    /**
     * 处理器ID
     */
    private String CPUID ;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFpVersion() {
        return fpVersion;
    }

    public void setFpVersion(String fpVersion) {
        this.fpVersion = fpVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDiskDriveSerial() {
        return diskDriveSerial;
    }

    public void setDiskDriveSerial(String diskDriveSerial) {
        this.diskDriveSerial = diskDriveSerial;
    }

    public String getBIOSSerial() {
        return BIOSSerial;
    }

    public void setBIOSSerial(String BIOSSerial) {
        this.BIOSSerial = BIOSSerial;
    }

    public String getBoardUUID() {
        return boardUUID;
    }

    public void setBoardUUID(String boardUUID) {
        this.boardUUID = boardUUID;
    }

    public String getMachineGUID() {
        return machineGUID;
    }

    public void setMachineGUID(String machineGUID) {
        this.machineGUID = machineGUID;
    }

    public String getCPUID() {
        return CPUID;
    }

    public void setCPUID(String CPUID) {
        this.CPUID = CPUID;
    }
}
