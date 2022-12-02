package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class BoardStatisticsResp implements Serializable {
    private static final long serialVersionUID = 4454818616173564961L;
    /**
     * 小组下人员数量
     */
    private Integer groupUserCount;
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
     * 该流向拦截数量
     */
    private Integer interceptCount;
    /**
     * 该流向下已组板列表（7日内未封车的板）
     */
    private List<BoardDto> boardDtoList;
    private Integer totalBoardCount;

    public Integer getGroupUserCount() {
        return groupUserCount;
    }

    public void setGroupUserCount(Integer groupUserCount) {
        this.groupUserCount = groupUserCount;
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

    public Integer getInterceptCount() {
        return interceptCount;
    }

    public void setInterceptCount(Integer interceptCount) {
        this.interceptCount = interceptCount;
    }

    public List<BoardDto> getBoardDtoList() {
        return boardDtoList;
    }

    public void setBoardDtoList(List<BoardDto> boardDtoList) {
        this.boardDtoList = boardDtoList;
    }

    public Integer getTotalBoardCount() {
        return totalBoardCount;
    }

    public void setTotalBoardCount(Integer totalBoardCount) {
        this.totalBoardCount = totalBoardCount;
    }
}
