package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class ValidateSortRelationReq implements Serializable {
    private static final long serialVersionUID = 5380129227311225455L;
    private String boxCode;
    private String packageCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
