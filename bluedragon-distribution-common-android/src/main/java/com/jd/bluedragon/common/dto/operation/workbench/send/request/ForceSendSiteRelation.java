package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import java.io.Serializable;

/**
 * @Desc : 强发场地关系
 * 常规场景，已知当前任务A, 流向C， 不在强发记录后，下次操作C流向直接强发A
 * 特殊场景：接货仓发货岗，发货混扫任务列表，没有具体可比流向，需要操作人员手动选择强发场地， 因此需要维护强发关系：A-B  A强发B, 保证下次遇到A能找到强发的目标流向
 * 混扫列表，强发关系是list
 * @Author zhengchengfa
 * @Date 2024/3/26 18:05
 * @Description
 */
public class ForceSendSiteRelation implements Serializable {
    private static final long serialVersionUID = 768445654929989093L;
    /**
     * 强发场地
     */
    private Integer forceSendSiteId;
    /**
     * 路由流向场地
     */
    private Integer routeNextSiteId;
    /**
     * 首次强发记录时间
     */
    private Long operateTime;

    public Integer getForceSendSiteId() {
        return forceSendSiteId;
    }

    public void setForceSendSiteId(Integer forceSendSiteId) {
        this.forceSendSiteId = forceSendSiteId;
    }

    public Integer getRouteNextSiteId() {
        return routeNextSiteId;
    }

    public void setRouteNextSiteId(Integer routeNextSiteId) {
        this.routeNextSiteId = routeNextSiteId;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
