package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class BindMiniStoreDeviceReq implements Serializable {
    private static final long serialVersionUID = -1270921018093707313L;
    private String miniStoreCode;
    private String iceBoardCode1;
    private String iceBoardCode2;
    private String boxCode;

    public String getMiniStoreCode() {
        return miniStoreCode;
    }

    public void setMiniStoreCode(String miniStoreCode) {
        this.miniStoreCode = miniStoreCode;
    }

    public String getIceBoardCode1() {
        return iceBoardCode1;
    }

    public void setIceBoardCode1(String iceBoardCode1) {
        this.iceBoardCode1 = iceBoardCode1;
    }

    public String getIceBoardCode2() {
        return iceBoardCode2;
    }

    public void setIceBoardCode2(String iceBoardCode2) {
        this.iceBoardCode2 = iceBoardCode2;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
