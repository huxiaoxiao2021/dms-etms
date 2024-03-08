package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;

public class CollectPackageResp implements Serializable {
    private static final long serialVersionUID = -1291589049030330347L;

    private Long endSiteId;

    private int materialType;

    public int getMaterialType() {
        return materialType;
    }

    public void setMaterialType(int materialType) {
        this.materialType = materialType;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }
}
