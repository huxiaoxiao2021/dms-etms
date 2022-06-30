package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class DeviceStatusValidateReq implements Serializable {
    private static final long serialVersionUID = 3999521323417453397L;
    private String storeCode;
    private String iceBoardCode;
    private String boxCode;

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
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
