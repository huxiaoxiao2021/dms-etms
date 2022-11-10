package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class SendFlowWaitScanDto implements Serializable {
    private static final long serialVersionUID = -115634624256034024L;

    private Integer endSiteId;
    private String endSiteName;
    /**
     * 滑道编号
     */
    private String crossCode;
    /**
     * 笼车编号
     */
    private String tableTrolleyCode;
    /**
     * 流向待扫包裹数量
     */
    private Integer packageCount;

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public String getTableTrolleyCode() {
        return tableTrolleyCode;
    }

    public void setTableTrolleyCode(String tableTrolleyCode) {
        this.tableTrolleyCode = tableTrolleyCode;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }
}
