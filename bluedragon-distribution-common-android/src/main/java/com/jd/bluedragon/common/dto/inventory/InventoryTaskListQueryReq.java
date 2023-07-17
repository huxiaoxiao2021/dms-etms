package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:56
 * @Description
 */
public class InventoryTaskListQueryReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    /**
     * 1: 查当日
     * 2: 查15天
     */
    private Integer queryDays;
    /**
     * 页码
     */
    private Integer pageNo;
    private Integer pageSize;
    /**
     * 仅查历史完成状态 true
     */
    private Boolean onlyHistoryComplete;


    public Integer getQueryDays() {
        return queryDays;
    }

    public void setQueryDays(Integer queryDays) {
        this.queryDays = queryDays;
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

    public Boolean getOnlyHistoryComplete() {
        return onlyHistoryComplete;
    }

    public void setOnlyHistoryComplete(Boolean onlyHistoryComplete) {
        this.onlyHistoryComplete = onlyHistoryComplete;
    }
}
