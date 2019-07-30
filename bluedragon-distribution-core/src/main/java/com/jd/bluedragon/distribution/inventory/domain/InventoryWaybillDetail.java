package com.jd.bluedragon.distribution.inventory.domain;

import java.io.Serializable;

public class InventoryWaybillDetail implements Serializable {

    /*
     * 包裹号
     * */
    private String packageCode;

    /*
     * 包裹号后缀
     * */
    private String packageCodeSuffix;


    /*
     * 包裹状态代码
     * */
    private String packageStatus;

    /*
     * 操作人erp
     * */
    private String operateUserErp;

    /*
    * 操作时间 MM-dd HH:mm
    * */
    private String operateTime;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageCodeSuffix() {
        return packageCodeSuffix;
    }

    public void setPackageCodeSuffix(String packageCodeSuffix) {
        this.packageCodeSuffix = packageCodeSuffix;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }
}
