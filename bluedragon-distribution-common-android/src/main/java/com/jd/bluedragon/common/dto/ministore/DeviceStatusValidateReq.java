package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class DeviceStatusValidateReq implements Serializable {
    private static final long serialVersionUID = 3999521323417453397L;
    private String miniStoreCode;
    private String iceBoardCode;
    private String boxCode;

    public String getMiniStoreCode() {
        return miniStoreCode;
    }

    public void setMiniStoreCode(String miniStoreCode) {
        this.miniStoreCode = miniStoreCode;
    }

    public String getIceBoardCode() {
        return iceBoardCode;
    }

    public void setIceBoardCode(String iceBoardCode) {
        this.iceBoardCode = iceBoardCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
