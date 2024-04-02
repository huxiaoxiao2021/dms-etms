package com.jd.bluedragon.common.dto.router.dynamicLine.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * 动态线路替换方案查询请求入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-02 10:38:58 周二
 */
public class RouterDynamicLineReplacePlanListReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -1028450085618939342L;

    private Integer pageNumber;
    private Integer pageSize;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
