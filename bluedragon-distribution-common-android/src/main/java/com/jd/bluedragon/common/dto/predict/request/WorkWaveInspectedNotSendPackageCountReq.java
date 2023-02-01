package com.jd.bluedragon.common.dto.predict.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WorkWaveInspectedNotSendPackageCountReq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 时间
     */
    private Date queryTime;
    /**
     * 当前场地
     */
    private Integer currentSiteCode;

    /**
     * 下游场地
     */
    private List<Integer> nextSiteCodes;

    private User user;

    private CurrentOperate currentOperate;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public List<Integer> getNextSiteCodes() {
        return nextSiteCodes;
    }

    public void setNextSiteCodes(List<Integer> nextSiteCodes) {
        this.nextSiteCodes = nextSiteCodes;
    }
}
