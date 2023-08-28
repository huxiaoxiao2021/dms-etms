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
    private List<Integer> statusCodeList;
    

    /**
     * 筛选条件
     */
    private FilterConditionDto filterConditionDto;
    /**
     * 关键词
     */
    private String keyword;

    /**
     * 来源 0 普通分页查询 1 推荐任务
     */
    private Integer source;

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
    

    public FilterConditionDto getFilterConditionDto() {
        return filterConditionDto;
    }

    public void setFilterConditionDto(FilterConditionDto filterConditionDto) {
        this.filterConditionDto = filterConditionDto;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Integer> getStatusCodeList() {
        return statusCodeList;
    }

    public void setStatusCodeList(List<Integer> statusCodeList) {
        this.statusCodeList = statusCodeList;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }
}
