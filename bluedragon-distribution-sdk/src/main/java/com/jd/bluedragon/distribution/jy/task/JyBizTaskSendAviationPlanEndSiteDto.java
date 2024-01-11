package com.jd.bluedragon.distribution.jy.task;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:06
 * @Description
 */
public class JyBizTaskSendAviationPlanEndSiteDto implements Serializable {

    private static final long serialVersionUID = 4089383783438643445L;


    private Integer nextSiteId;

    private String nextSiteName;

    private Integer nextSiteTaskNum;

    public Integer getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Integer nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Integer getNextSiteTaskNum() {
        return nextSiteTaskNum;
    }

    public void setNextSiteTaskNum(Integer nextSiteTaskNum) {
        this.nextSiteTaskNum = nextSiteTaskNum;
    }
}