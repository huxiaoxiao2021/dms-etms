package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SendFlowDto implements Serializable {
    private static final long serialVersionUID = 1663772914468225864L;
    private Integer startSiteId;
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
     * 该流向拦截数量
     */
    private Integer interceptCount;
    /**
     * 该流向组板数量
     */
    private Integer boardCount;
    /**
     * 该流向已扫包裹数量
     */
    private Integer packageHaveScanCount;
    /**
     * 该流向已扫箱子数量
     */
    private Integer boxHaveScanCount;
    /**
     * 该流向待扫数量
     */
    private Integer waitScanCount;
    /**
     *该流向进行中的板详情
     */
    private BoardDto currentBoardDto;

    /**
     * 未封车时间
     */
    private Date queryTimeBegin;

    /**
     * 已封车时间
     */
    private Date querySealTimeBegin;
    
    private List<Integer> comboardSourceList;

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

    public Integer getPackageHaveScanCount() {
        return packageHaveScanCount;
    }

    public void setPackageHaveScanCount(Integer packageHaveScanCount) {
        this.packageHaveScanCount = packageHaveScanCount;
    }

    public Integer getBoxHaveScanCount() {
        return boxHaveScanCount;
    }

    public void setBoxHaveScanCount(Integer boxHaveScanCount) {
        this.boxHaveScanCount = boxHaveScanCount;
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

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Date getQueryTimeBegin() {
        return queryTimeBegin;
    }

    public void setQueryTimeBegin(Date queryTimeBegin) {
        this.queryTimeBegin = queryTimeBegin;
    }

    public List<Integer> getComboardSourceList() {
        return comboardSourceList;
    }

    public void setComboardSourceList(List<Integer> comboardSourceList) {
        this.comboardSourceList = comboardSourceList;
    }

    public Date getQuerySealTimeBegin() {
        return querySealTimeBegin;
    }

    public void setQuerySealTimeBegin(Date querySealTimeBegin) {
        this.querySealTimeBegin = querySealTimeBegin;
    }
}
