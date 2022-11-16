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
    /**
     * 该流向拦截数量
     */
    private Integer interceptCount;
    /**
     * 该流向组板数量
     */
    private Integer boardCount;

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

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public Integer getBoardCount() {
        return boardCount;
    }

    public void setBoardCount(Integer boardCount) {
        this.boardCount = boardCount;
    }
}
