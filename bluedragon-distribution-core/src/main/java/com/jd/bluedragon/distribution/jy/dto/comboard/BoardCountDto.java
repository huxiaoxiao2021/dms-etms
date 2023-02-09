package com.jd.bluedragon.distribution.jy.dto.comboard;

/**
 * @author liwenji
 * @date 2022-12-02 11:27
 */

public class BoardCountDto {
    
    private Integer boardCount;
    
    private Long endSiteId;
    

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteCode) {
        this.endSiteId = endSiteCode;
    }

    public Integer getBoardCount() {
        return boardCount;
    }

    public void setBoardCount(Integer boardCount) {
        this.boardCount = boardCount;
    }
}
