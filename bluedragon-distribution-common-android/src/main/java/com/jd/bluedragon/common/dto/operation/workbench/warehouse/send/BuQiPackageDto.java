package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;

public class BuQiPackageDto implements Serializable {


    private static final long serialVersionUID = -5809332610524693231L;


    private String waybillCode;
    private String packageCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
