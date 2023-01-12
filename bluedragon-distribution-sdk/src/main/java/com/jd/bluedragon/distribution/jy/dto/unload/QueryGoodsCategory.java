package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class QueryGoodsCategory extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -3384319549017831122L;
    /**
     * 任务bizId
     */
    private String bizId;
    /**
     * 板号
     */
    private String boardCode;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
