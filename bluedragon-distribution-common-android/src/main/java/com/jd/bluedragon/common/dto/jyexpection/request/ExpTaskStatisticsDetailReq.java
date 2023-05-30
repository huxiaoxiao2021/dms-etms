package com.jd.bluedragon.common.dto.jyexpection.request;

import java.util.Date;

public class ExpTaskStatisticsDetailReq extends ExpBaseReq {


    /**
     * 异常类型0：三无 1：报废 2：破损
     */
    private Integer type;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 网格编号
      */
    private String gridCode;

    /**
     * 网格号
     */
    private String gridNo;

    /**
     * 作业区编码
     */
    private String areaCode;

    /**
     * 网格
     */
    private String gridRid;

    /**
     * 超时时间
     */
    private Date timeOutTime;


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGridRid() {
        return gridRid;
    }

    public void setGridRid(String gridRid) {
        this.gridRid = gridRid;
    }

    public Date getTimeOutTime() {
        return timeOutTime;
    }

    public void setTimeOutTime(Date timeOutTime) {
        this.timeOutTime = timeOutTime;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
}
