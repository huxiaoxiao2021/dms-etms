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

    private String space1;

    private String space2;

    private String space3;

    private String space4;

    private String space5;

    private String space6;

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

    public String getSpace1() {
        return space1;
    }

    public void setSpace1(String space1) {
        this.space1 = space1;
    }

    public String getSpace2() {
        return space2;
    }

    public void setSpace2(String space2) {
        this.space2 = space2;
    }

    public String getSpace3() {
        return space3;
    }

    public void setSpace3(String space3) {
        this.space3 = space3;
    }

    public String getSpace4() {
        return space4;
    }

    public void setSpace4(String space4) {
        this.space4 = space4;
    }

    public String getSpace5() {
        return space5;
    }

    public void setSpace5(String space5) {
        this.space5 = space5;
    }

    public String getSpace6() {
        return space6;
    }

    public void setSpace6(String space6) {
        this.space6 = space6;
    }
}
