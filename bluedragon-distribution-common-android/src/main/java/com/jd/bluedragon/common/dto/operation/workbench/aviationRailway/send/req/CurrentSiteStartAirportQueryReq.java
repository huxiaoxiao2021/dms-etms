package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/4 15:30
 * @Description
 */
public class CurrentSiteStartAirportQueryReq  extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private Integer aviationType;

    private Integer pageNo;
    private Integer pageSize;

    public Integer getAviationType() {
        return aviationType;
    }

    public void setAviationType(Integer aviationType) {
        this.aviationType = aviationType;
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
