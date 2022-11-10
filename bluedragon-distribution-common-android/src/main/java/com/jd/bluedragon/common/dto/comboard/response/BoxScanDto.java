package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class BoxScanDto implements Serializable {
    private static final long serialVersionUID = 3605310597726635355L;
    private String boxCode;
    private String packageCount;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(String packageCount) {
        this.packageCount = packageCount;
    }
}
