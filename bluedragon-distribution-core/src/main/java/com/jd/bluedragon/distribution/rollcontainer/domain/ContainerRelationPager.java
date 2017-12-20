package com.jd.bluedragon.distribution.rollcontainer.domain;

/**
 * Created by jinjingcheng on 2017/12/20.
 */
public class ContainerRelationPager extends ContainerRelation{
    private String startTime;
    private String endTime;
    private Integer page;
    private Integer pageSize;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
