package com.jd.bluedragon.common.dto.collectpackage.request;

import java.io.Serializable;

public class StatisticsUnderFlowQueryReq extends StatisticsUnderTaskQueryReq implements Serializable {
    private static final long serialVersionUID = 2867693452211558125L;

    private Long endSiteId;

    private Integer pageNo;

    private Integer pageSize;

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

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }
}
