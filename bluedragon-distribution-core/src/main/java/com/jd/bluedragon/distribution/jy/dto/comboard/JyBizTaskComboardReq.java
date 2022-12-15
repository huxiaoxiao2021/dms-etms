package com.jd.bluedragon.distribution.jy.dto.comboard;

import lombok.Data;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-11-23 11:56
 */
public class JyBizTaskComboardReq {
    
    /**
     * 更新人姓名
     */
    private String updateUserErp;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 始发站点
     */
    private Integer startSiteId;

    /**
     * 目的地站点
     */
    private List<Integer> endSiteCodeList;

    /**
     * 板号
     */
    private String boardCode;
    
    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public List<Integer> getEndSiteCodeList() {
        return endSiteCodeList;
    }

    public void setEndSiteCodeList(List<Integer> endSiteCodeList) {
        this.endSiteCodeList = endSiteCodeList;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
