package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/8 17:48
 * @Description
 */
public class AviationSendTaskQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private Integer pageNo;
    private Integer pageSize;
    /**
     * 查询分页开始时间
     */
    private Long queryPageTime;
    private Integer nextSiteId;

    /**
     * JyAviationRailwaySendVehicleStatusEnum.sendTaskStatus
     */
    private List<Integer> statusCode;
    
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

    public Long getQueryPageTime() {
        return queryPageTime;
    }

    public void setQueryPageTime(Long queryPageTime) {
        this.queryPageTime = queryPageTime;
    }

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public List<Integer> getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(List<Integer> statusCode) {
        this.statusCode = statusCode;
    }
}
