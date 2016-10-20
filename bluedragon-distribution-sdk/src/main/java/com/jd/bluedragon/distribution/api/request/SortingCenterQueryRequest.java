package com.jd.bluedragon.distribution.api.request;

/**
 * Created by wuzuxiang on 2016/10/17.
 */
public class SortingCenterQueryRequest<T> {

    private String siteNo;/** 分拣中心ID **/

    private String tableName;/** 查询的表名 **/

    private String startTime;/** 开始时间 **/

    private String endTime;/** 结束时间 **/

    private T expTypeList;/** EXP_TYPE值 **/

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

    public T getExpTypeList() {
        return expTypeList;
    }

    public void setExpTypeList(T expTypeList) {
        this.expTypeList = expTypeList;
    }
}
