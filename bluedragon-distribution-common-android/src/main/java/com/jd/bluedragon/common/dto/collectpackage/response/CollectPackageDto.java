package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

public class CollectPackageDto implements Serializable {
    private static final long serialVersionUID = -3849581807474694437L;

    /**
     * 包裹号
     */
    private String packageCode;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
