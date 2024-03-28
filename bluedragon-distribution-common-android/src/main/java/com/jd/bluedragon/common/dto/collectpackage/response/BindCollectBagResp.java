package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

public class BindCollectBagResp implements Serializable {
    private static final long serialVersionUID = 3583746186689096978L;

    private int materialType;

    public int getMaterialType() {
        return materialType;
    }

    public void setMaterialType(int materialType) {
        this.materialType = materialType;
    }
}
