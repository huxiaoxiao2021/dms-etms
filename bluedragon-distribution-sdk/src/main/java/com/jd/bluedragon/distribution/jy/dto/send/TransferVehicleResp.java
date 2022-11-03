package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

public class TransferVehicleResp implements Serializable {

    private static final long serialVersionUID = -3631878177602459614L;

    /**
     * 需要迁移的批次号
     */
    private List<String> fromSendCodes;

    /**
     * 新生成的批次号
     */
    private List<String> toSendCodes;

    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 目的场地名称
     */
    private String endSiteName;

    public List<String> getFromSendCodes() {
        return fromSendCodes;
    }

    public void setFromSendCodes(List<String> fromSendCodes) {
        this.fromSendCodes = fromSendCodes;
    }

    public List<String> getToSendCodes() {
        return toSendCodes;
    }

    public void setToSendCodes(List<String> toSendCodes) {
        this.toSendCodes = toSendCodes;
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
