package com.jd.bluedragon.common.dto.collectpackage.request;

import java.io.Serializable;

public class BindCollectBagReq implements Serializable {
    private static final long serialVersionUID = 5886034403638777749L;
    private String boxCode;
    private String materialCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
}
