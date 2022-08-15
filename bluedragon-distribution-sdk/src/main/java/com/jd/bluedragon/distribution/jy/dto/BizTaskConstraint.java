package com.jd.bluedragon.distribution.jy.dto;

public class BizTaskConstraint {

    /**
     * 站点编号
     */
    private Long siteCode;
    /**
     * 楼层
     */
    private Integer floor;
    /**
     * 工作区
     */
    private String areaCode;
    /**
     * 网格
     */
    private String gridCode;
    /**
     * 工序
     */
    private String workCode;

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }
}
