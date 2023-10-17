package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/15 14:44
 * @Description
 */
public class ShuttleSendTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 4784612639942744950L;

    private Integer pageNo;

    private Integer pageSize;

    private String keyword;
    /**
     * 摆渡发车任务查询来源
     * ShuttleQuerySourceEnum
     */
    private Integer shuttleQuerySource;

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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getShuttleQuerySource() {
        return shuttleQuerySource;
    }

    public void setShuttleQuerySource(Integer shuttleQuerySource) {
        this.shuttleQuerySource = shuttleQuerySource;
    }
}
