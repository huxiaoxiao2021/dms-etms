package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class CancelSendTaskResp implements Serializable {
    private static final long serialVersionUID = 3813878446640603317L;
    //流向目的站点名称
    private String endSiteName;
    //取消的包裹数量
    private Integer canclePackageCount;

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Integer getCanclePackageCount() {
        return canclePackageCount;
    }

    public void setCanclePackageCount(Integer canclePackageCount) {
        this.canclePackageCount = canclePackageCount;
    }
}
