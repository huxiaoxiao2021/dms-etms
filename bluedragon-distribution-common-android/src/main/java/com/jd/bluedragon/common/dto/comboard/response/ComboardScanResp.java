package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class ComboardScanResp implements Serializable {
    private String endSiteId;

    public String getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(String endSiteId) {
        this.endSiteId = endSiteId;
    }
}
