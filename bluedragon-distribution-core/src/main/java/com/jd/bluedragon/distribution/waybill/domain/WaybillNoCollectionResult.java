package com.jd.bluedragon.distribution.waybill.domain;

import java.util.List;

public class WaybillNoCollectionResult {

    private int total;

    private List<String> packageCodeList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<String> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<String> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }
}
