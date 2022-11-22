package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class BoardReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6056651432686595714L;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 大宗标识
     */
    private boolean bulkFlag;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public boolean isBulkFlag() {
        return bulkFlag;
    }

    public void setBulkFlag(boolean bulkFlag) {
        this.bulkFlag = bulkFlag;
    }
}
