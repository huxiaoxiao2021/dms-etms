package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class ComBoardDto implements Serializable {
    private static final long serialVersionUID = 8723860956859377453L;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 板下已扫包裹数量
     */
    private Integer haveScanCount;
    /**
     * 板下多扫包裹数量
     */
    private Integer extraScanCount;


    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
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
