package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;

/**
 * Created by yangbo7 on 2016/3/28.
 */
public class RemoteAccessRequest extends JdObject {

    private static final long serialVersionUID = -7800839106682310757L;

    // 运单或者包裹号
    private String waybillOrPackageCode;

    // 分拣机机器码
    private String machineCode;

    // 本地分拣中心的URL
    private String localDmsUrl;

    public RemoteAccessRequest() {
    }

    public String getWaybillOrPackageCode() {
        return waybillOrPackageCode;
    }

    public void setWaybillOrPackageCode(String waybillOrPackageCode) {
        this.waybillOrPackageCode = waybillOrPackageCode;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getLocalDmsUrl() {
        return localDmsUrl;
    }

    public void setLocalDmsUrl(String localDmsUrl) {
        this.localDmsUrl = localDmsUrl;
    }
}
