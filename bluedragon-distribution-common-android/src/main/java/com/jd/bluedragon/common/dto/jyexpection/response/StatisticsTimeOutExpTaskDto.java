package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/6/2 10:25
 * @Description: 超时未领取任务统计Dto
 */
public class StatisticsTimeOutExpTaskDto implements Serializable {

    //站点id
    private Long siteCode;

    //站点名称
    private String siteName;

    // 楼层
    private Integer floor;

    // 网格号
    private String gridNo;

    // 网格编码
    private String gridCode;

    // 作业区编号
    private String areaCode;

    // 作业区
    private String areaName;


    // 超时数量
    private Integer timeoutNum;


    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getTimeoutNum() {
        return timeoutNum;
    }

    public void setTimeoutNum(Integer timeoutNum) {
        this.timeoutNum = timeoutNum;
    }
}
