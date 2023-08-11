package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/8/11 18:30
 * @Description
 */
public class AviationNextSiteStatisticsDto implements Serializable {

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
