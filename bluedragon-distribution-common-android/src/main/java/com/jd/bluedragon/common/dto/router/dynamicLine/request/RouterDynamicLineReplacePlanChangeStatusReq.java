package com.jd.bluedragon.common.dto.router.dynamicLine.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * 动态线路替换方案修改状态请求入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-02 10:38:58 周二
 */
public class RouterDynamicLineReplacePlanChangeStatusReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -1028450085618939342L;

    private Long id;

    private Integer enableStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(Integer enableStatus) {
        this.enableStatus = enableStatus;
    }

}
