package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class BindCollectBagReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 5886034403638777749L;
    private String boxCode;
    private String materialCode;
    private boolean forceBindFlag;

    public boolean getForceBindFlag() {
        return forceBindFlag;
    }

    public void setForceBindFlag(boolean forceBindFlag) {
        this.forceBindFlag = forceBindFlag;
    }

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
