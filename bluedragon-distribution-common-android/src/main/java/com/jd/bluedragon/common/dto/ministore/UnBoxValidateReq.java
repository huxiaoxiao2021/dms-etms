package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class UnBoxValidateReq implements Serializable {
    private static final long serialVersionUID = -7084771505168281914L;
    private String storeCode;
    private String iceBoardCode1;
    private String iceBoardCode2;
    private String boxCode;

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
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
