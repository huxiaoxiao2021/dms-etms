package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

public class TransferVehicleResp implements Serializable {

    private static final long serialVersionUID = -3631878177602459614L;

    /**
     * 迁移后的批次号
     */
    private List<String> sendCodes;

    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 目的场地名称
     */
    private String endSiteName;

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }
}
