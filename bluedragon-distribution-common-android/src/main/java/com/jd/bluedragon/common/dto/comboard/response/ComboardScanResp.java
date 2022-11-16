package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class ComboardScanResp implements Serializable {
    private Integer endSiteId;

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }
}
