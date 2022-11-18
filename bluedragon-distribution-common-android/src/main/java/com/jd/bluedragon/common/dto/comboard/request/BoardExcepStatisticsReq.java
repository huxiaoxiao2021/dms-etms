package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class BoardExcepStatisticsReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 597519678244355595L;
    private String boardCode;
    /**
     * 异常类型
     */
    private Integer excepType;
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

    public Integer getExcepType() {
        return excepType;
    }

    public void setExcepType(Integer excepType) {
        this.excepType = excepType;
    }
}
