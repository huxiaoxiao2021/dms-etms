package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class HaveScanStatisticsReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 5027851101783439295L;
    private String boardCode;
    private Integer pageNo;
    private Integer pageSize;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
