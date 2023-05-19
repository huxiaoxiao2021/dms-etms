package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.util.List;

public class BuQiPackageRes implements Serializable {


    private static final long serialVersionUID = -5809332610524693231L;

    private List<String> packageList;

    public List<String> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }
}
