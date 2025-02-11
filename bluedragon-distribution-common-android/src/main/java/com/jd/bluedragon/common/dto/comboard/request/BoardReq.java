package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class BoardReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6056651432686595714L;
    //  对外暴露的枚举 BusinessCodeFromSourceEnum
    private String bizSource = "JY_APP";
    /**
     * 板号
     */
    private String boardCode;

    private Integer endSiteId;
    /**
     * 大宗标识
     */
    private boolean bulkFlag;

    public String getBizSource() {
        return bizSource;
    }

    public void setBizSource(String bizSource) {
        this.bizSource = bizSource;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public boolean getBulkFlag() {
        return bulkFlag;
    }

    public void setBulkFlag(boolean bulkFlag) {
        this.bulkFlag = bulkFlag;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }
}
