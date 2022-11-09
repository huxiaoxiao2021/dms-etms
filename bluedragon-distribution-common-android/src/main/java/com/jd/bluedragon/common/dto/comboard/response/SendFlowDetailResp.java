package com.jd.bluedragon.common.dto.comboard.response;


import java.io.Serializable;

public class SendFlowDetailResp implements Serializable {
    private static final long serialVersionUID = -3031759375359452317L;
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
     * 该流向组板数量
     */
    private Integer boardCount;
    /**
     * 该流向待扫数量
     */
    private Integer waitScanCount;
    /**
     *该流向进行中的板详情
     */
    private BoardDto currentBoardDto;

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

    public Integer getBoardCount() {
        return boardCount;
    }

    public void setBoardCount(Integer boardCount) {
        this.boardCount = boardCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }

    public BoardDto getCurrentBoardDto() {
        return currentBoardDto;
    }

    public void setCurrentBoardDto(BoardDto currentBoardDto) {
        this.currentBoardDto = currentBoardDto;
    }
}
