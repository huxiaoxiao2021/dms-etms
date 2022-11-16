package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class TableTrolleyReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 2913336393003471073L;
    /**
     * 滑道编号
     */
    private String crossCode;
    private boolean needStatistics;
    private Integer pageNo;
    private Integer pageSize;
    
    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public boolean isNeedStatistics() {
        return needStatistics;
    }

    public void setNeedStatistics(boolean needStatistics) {
        this.needStatistics = needStatistics;
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
