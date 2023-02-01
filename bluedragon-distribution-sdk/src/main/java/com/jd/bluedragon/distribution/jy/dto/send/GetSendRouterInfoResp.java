package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

public class GetSendRouterInfoResp implements Serializable {
    private static final long serialVersionUID = 982047975897612102L;
    private String endSiteId;
    private String endSiteName;

    public String getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(String endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }
}
