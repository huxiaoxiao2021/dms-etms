package com.jd.bluedragon.distribution.waybill.domain;

import java.util.List;

public class InspectionNoCollectionResult {

    private int waybillTotal;

    private int packageTotal;

    private List<String> packageCodeList;

    public int getWaybillTotal() {
        return waybillTotal;
    }

    public void setWaybillTotal(int waybillTotal) {
        this.waybillTotal = waybillTotal;
    }

    public int getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(int packageTotal) {
        this.packageTotal = packageTotal;
    }

    public List<String> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<String> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }
}
