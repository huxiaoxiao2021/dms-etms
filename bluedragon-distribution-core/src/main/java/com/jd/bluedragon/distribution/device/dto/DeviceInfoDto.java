package com.jd.bluedragon.distribution.device.dto;

/**
 * <p>
 *     设备信息对象简化版，按需添加字段
 *
 * @author wuzuxiang
 * @since 2019/11/27
 **/
public class DeviceInfoDto {

    /**
     * 设备号
     */
    private String machineCode;

    /**
     * 设备创建站点
     */
    private String siteCode;

    /**
     * 设备类型编号
     */
    private String deviceTypeCode;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 是否启用
     */
    private Integer isEnable;

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(String deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }
}
