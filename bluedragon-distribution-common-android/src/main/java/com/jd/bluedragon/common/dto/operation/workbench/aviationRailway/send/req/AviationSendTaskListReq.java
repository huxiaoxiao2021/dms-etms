package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:59
 * @Description
 */
public class AviationSendTaskListReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    /**
     * 列表状态：
     * JyAviationRailwaySendVehicleStatusEnum
     */
    private Integer statusCode;
    /**
     * 筛选条件
     */
    private FilterConditionDto filterConditionDto;
    /**
     * 关键词
     */
    private String keyword;


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
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

}
