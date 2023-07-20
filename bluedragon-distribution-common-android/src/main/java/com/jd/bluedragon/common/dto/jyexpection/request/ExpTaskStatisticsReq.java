package com.jd.bluedragon.common.dto.jyexpection.request;

import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/29 17:34
 * @Description: 超时未领取 req
 */
public class ExpTaskStatisticsReq extends ExpBaseReq{

    /**
     * 异常类型0：三无 1：报废 2：破损
     */
    private Integer type;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 网格
     */
    private String gridRid;

    /**
     * 超时时间
     */
    private Date timeOutTime;

    private Integer pageNumber;

    private Integer pageSize;

    private Integer offSet;

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

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffSet() {
        if (pageNumber == null || pageSize == null) {
            return 0;
        }
        return (pageNumber - 1) * pageSize;
    }

    public void setOffSet(Integer offSet) {
        if (pageNumber == null || pageSize == null) {
            this.offSet = 0;
        }else {
            this.offSet = (pageNumber - 1) * pageSize;
        }
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getTimeOutTime() {
        return timeOutTime;
    }

    public void setTimeOutTime(Date timeOutTime) {
        this.timeOutTime = timeOutTime;
    }
}
