package com.jd.bluedragon.common.dto.wastepackagestorage.response;

import java.io.Serializable;

public class WastePackageStatusResponse  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态值
     */
    private int statusCode;

    /**
     * 显示的状态名称
     */
    private String statusName;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
