package com.jd.bluedragon.common.dto.jyexpection.request;

/**
 * 按网格统计
 */
public class StatisticsByGridReq extends ExpBaseReq {

    private Integer status;

    private String gridRid;

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
}
