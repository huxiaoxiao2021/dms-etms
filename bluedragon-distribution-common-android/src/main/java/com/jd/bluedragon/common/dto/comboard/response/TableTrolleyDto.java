package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class TableTrolleyDto implements Serializable {
    private static final long serialVersionUID = 8123446470608364924L;
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
     * 选中状态
     */
    private boolean selectedFlag;

    public boolean isSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(boolean selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

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

    public String getTableTrolleyCode() {
        return tableTrolleyCode;
    }

    public void setTableTrolleyCode(String tableTrolleyCode) {
        this.tableTrolleyCode = tableTrolleyCode;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }
}
