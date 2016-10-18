package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2016/10/17.
 */
public class SortingCenterQueryRequest implements Serializable {

    private static final long serialVersionUID = 5782349878467382942L;

    private String siteNo;/** 分拣中心ID **/

    private String tableName;/** 查询的表名 **/

    private Date startTime;/** 分拣中心ID **/

    private Date endTime;/** 分拣中心ID **/

    private List<String> expTypeList;/** 分拣中心ID **/

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getExpTypeList() {
        return expTypeList;
    }

    public void setExpTypeList(List<String> expTypeList) {
        this.expTypeList = expTypeList;
    }
}
