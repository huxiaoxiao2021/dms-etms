package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class PackageScanDto implements Serializable {
    private static final long serialVersionUID = -6303539269882493784L;
    private String packageCount;

    public String getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(String packageCount) {
        this.packageCount = packageCount;
    }
}
