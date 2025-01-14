package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
import java.util.List;

public class TaskFlowComBoardDto implements Serializable {
    private static final long serialVersionUID = -7931113868979801874L;
    private List<ComBoardAggDto> comBoardDtoList;
    /**
     * 流向下已扫包裹数量
     */
    private Integer haveScanCount;
    /**
     * 流向下多扫包裹数量
     */
    private Integer extraScanCount;

    public List<ComBoardAggDto> getComBoardDtoList() {
        return comBoardDtoList;
    }

    public void setComBoardDtoList(List<ComBoardAggDto> comBoardDtoList) {
        this.comBoardDtoList = comBoardDtoList;
    }

    public Integer getHaveScanCount() {
        return haveScanCount;
    }

    public void setHaveScanCount(Integer haveScanCount) {
        this.haveScanCount = haveScanCount;
    }

    public Integer getExtraScanCount() {
        return extraScanCount;
    }

    public void setExtraScanCount(Integer extraScanCount) {
        this.extraScanCount = extraScanCount;
    }

}
