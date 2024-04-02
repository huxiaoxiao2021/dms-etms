package com.jd.bluedragon.common.dto.router.dynamicLine.request;

import com.jd.bluedragon.common.dto.base.request.OperateUser;

import java.io.Serializable;

/**
 * 动态线路替换方案修改状态请求入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-02 10:38:58 周二
 */
public class RouterDynamicLineReplacePlanChangeStatusReq implements Serializable {
    private static final long serialVersionUID = -1028450085618939342L;

    private Integer status;

    private OperateUser operateUser;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
    }
}
