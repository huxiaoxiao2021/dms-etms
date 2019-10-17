package com.jd.bluedragon.distribution.rest.dap;

import java.io.Serializable;
import java.util.Date;

public class DapInfo implements Serializable {

    private String tableName;

    private Date createTime;

    private Date updateTime;

    private Date ts;

    private Integer createTimeGap;

    private Integer tsGap;

    private Integer updateDateGap;

    private String rowCount;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getCreateTimeGap() {
        return createTimeGap;
    }

    public void setCreateTimeGap(Integer createTimeGap) {
        this.createTimeGap = createTimeGap;
    }

    public Integer getTsGap() {
        return tsGap;
    }

    public void setTsGap(Integer tsGap) {
        this.tsGap = tsGap;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateDateGap() {

        return updateDateGap;
    }

    public void setUpdateDateGap(Integer updateDateGap) {
        this.updateDateGap = updateDateGap;
    }

    public String getRowCount() {
        return rowCount;
    }

    public void setRowCount(String rowCount) {
        this.rowCount = rowCount;
    }
}
