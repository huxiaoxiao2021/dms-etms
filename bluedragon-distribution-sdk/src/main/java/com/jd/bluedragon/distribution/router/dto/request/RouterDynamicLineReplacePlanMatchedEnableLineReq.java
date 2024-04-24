package com.jd.bluedragon.distribution.router.dto.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 根据条件查询可用的动态线路替换方案请求入参
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-03 17:54:12 周三
 */
public class RouterDynamicLineReplacePlanMatchedEnableLineReq implements Serializable {
    private static final long serialVersionUID = 307073225798645551L;

    /**
     * 始发场地
     */
    private Integer startSiteId;

    /**
     * 原目的场地
     */
    private Integer oldEndSiteId;

    /**
     * 新目的场地
     */
    private Integer newEndSiteId;

    /**
     * 作用时间，不传默认取当前系统时间
     */
    private Date effectTime;

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Integer getOldEndSiteId() {
        return oldEndSiteId;
    }

    public void setOldEndSiteId(Integer oldEndSiteId) {
        this.oldEndSiteId = oldEndSiteId;
    }

    public Integer getNewEndSiteId() {
        return newEndSiteId;
    }

    public void setNewEndSiteId(Integer newEndSiteId) {
        this.newEndSiteId = newEndSiteId;
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }
}
