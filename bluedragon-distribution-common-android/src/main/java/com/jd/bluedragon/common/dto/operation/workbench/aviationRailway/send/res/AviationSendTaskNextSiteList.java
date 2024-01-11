package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;

/**
 * 发货封车任务响应
 **/
public class AviationSendTaskNextSiteList implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;


    private Integer totalNum;

    private Integer nextSiteId;
    private String nextSiteName;



    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

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
}
