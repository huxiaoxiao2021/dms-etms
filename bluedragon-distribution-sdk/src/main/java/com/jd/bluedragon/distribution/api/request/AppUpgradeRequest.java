package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @ClassName AppUpgradeRequest
 * @Description
 * @Author wyh
 * @Date 2022/3/12 14:17
 **/
public class AppUpgradeRequest implements Serializable {

    /**
     * 用户ERP
     */
    private String erpAccount;

    /**
     * 场地
     */
    private Integer siteCode;

    /**
     * 版本号
     */
    private String versionCode;

    /**
     * 设备编号
     */
    private String deviceId;

    public String getErpAccount() {
        return erpAccount;
    }

    public void setErpAccount(String erpAccount) {
        this.erpAccount = erpAccount;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
