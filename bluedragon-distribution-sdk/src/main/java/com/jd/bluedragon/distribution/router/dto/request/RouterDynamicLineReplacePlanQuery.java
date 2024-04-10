package com.jd.bluedragon.distribution.router.dto.request;


import com.jd.dms.java.utils.sdk.base.Page;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: <br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2024-04-01 19:35:04 周一
 */
public class RouterDynamicLineReplacePlanQuery extends Page implements Serializable {

    private static final long serialVersionUID = 7340503050913597627L;

    private Long id;

    /**
     * 版本ID
     */
    private Long versionId;

    /**
     * 起始场地ID
     */
    private Integer startSiteId;

    /**
     * 原始目的场地ID
     */
    private Integer oldEndSiteId;

    /**
     * 替换目的场地ID
     */
    private Integer newEndSiteId;

    /**
     * 匹配状态列表
     */
    private List<Integer> enableStatusList;

    private Date effectTime;
    private Date effectTimeStart;
    private Date effectTimeEnd;

    private Long pushTime;

    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

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

    public List<Integer> getEnableStatusList() {
        return enableStatusList;
    }

    public void setEnableStatusList(List<Integer> enableStatusList) {
        this.enableStatusList = enableStatusList;
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    public Date getEffectTimeStart() {
        return effectTimeStart;
    }

    public void setEffectTimeStart(Date effectTimeStart) {
        this.effectTimeStart = effectTimeStart;
    }

    public Date getEffectTimeEnd() {
        return effectTimeEnd;
    }

    public void setEffectTimeEnd(Date effectTimeEnd) {
        this.effectTimeEnd = effectTimeEnd;
    }

    public Long getPushTime() {
        return pushTime;
    }

    public void setPushTime(Long pushTime) {
        this.pushTime = pushTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    @Override
    public String toString() {
        return "RouterDynamicLineReplacePlanQuery{" +
                "id=" + id +
                ", versionId=" + versionId +
                ", startSiteId=" + startSiteId +
                ", oldEndSiteId=" + oldEndSiteId +
                ", newEndSiteId=" + newEndSiteId +
                ", enableStatusList=" + enableStatusList +
                ", pushTime=" + pushTime +
                ", yn=" + yn +
                '}';
    }
}