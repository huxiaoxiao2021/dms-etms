package com.jd.bluedragon.distribution.waybill.domain;

public class WaybillNoCollectionInfo {

    private String waybillCode;

    private String packageCodeSomeone;

    private int scanCount;

    private int packageNum;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCodeSomeone() {
        return packageCodeSomeone;
    }

    public void setPackageCodeSomeone(String packageCodeSomeone) {
        this.packageCodeSomeone = packageCodeSomeone;
    }

    public int getScanCount() {
        return scanCount;
    }

    public void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    public int getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(int packageNum) {
        this.packageNum = packageNum;
    }
}
