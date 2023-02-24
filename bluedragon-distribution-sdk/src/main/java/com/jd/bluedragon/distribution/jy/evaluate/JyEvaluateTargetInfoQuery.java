package com.jd.bluedragon.distribution.jy.evaluate;

import java.util.Date;

public class JyEvaluateTargetInfoQuery {
    /**
     * 被评价场地装车日期--查询开始时间
     */
    private Date targetStartTime;
    /**
     * 被评价场地装车日期--查询结束时间
     */
    private Date targetEndTime;
    /**
     * 被评价场地所处区域
     */
    private Integer targetAreaCode;
    /**
     * 被评价场地
     */
    private Integer targetSiteCode;
    /**
     * 车牌号
     */
    private String vehicleNumber;
    /**
     * 评价时间--开始查询时间
     */
    private Date evaluateStartTime;
    /**
     * 评价时间--结束查询时间
     */
    private Date evaluateEndTime;
    /**
     * 评价来源区域
     */
    private Integer sourceAreaCode;
    /**
     * 评价来源场地
     */
    private Integer sourceSiteCode;
    /**
     * 协助人erp
     */
    private String helperErp;

    private Integer pageNumber;

    private Integer pageSize;

    private int limit = 10;

    private int offset = 0;

    public Date getTargetStartTime() {
        return targetStartTime;
    }

    public void setTargetStartTime(Date targetStartTime) {
        this.targetStartTime = targetStartTime;
    }

    public Date getTargetEndTime() {
        return targetEndTime;
    }

    public void setTargetEndTime(Date targetEndTime) {
        this.targetEndTime = targetEndTime;
    }

    public Integer getTargetAreaCode() {
        return targetAreaCode;
    }

    public void setTargetAreaCode(Integer targetAreaCode) {
        this.targetAreaCode = targetAreaCode;
    }

    public Integer getTargetSiteCode() {
        return targetSiteCode;
    }

    public void setTargetSiteCode(Integer targetSiteCode) {
        this.targetSiteCode = targetSiteCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getEvaluateStartTime() {
        return evaluateStartTime;
    }

    public void setEvaluateStartTime(Date evaluateStartTime) {
        this.evaluateStartTime = evaluateStartTime;
    }

    public Date getEvaluateEndTime() {
        return evaluateEndTime;
    }

    public void setEvaluateEndTime(Date evaluateEndTime) {
        this.evaluateEndTime = evaluateEndTime;
    }

    public Integer getSourceAreaCode() {
        return sourceAreaCode;
    }

    public void setSourceAreaCode(Integer sourceAreaCode) {
        this.sourceAreaCode = sourceAreaCode;
    }

    public Integer getSourceSiteCode() {
        return sourceSiteCode;
    }

    public void setSourceSiteCode(Integer sourceSiteCode) {
        this.sourceSiteCode = sourceSiteCode;
    }

    public String getHelperErp() {
        return helperErp;
    }

    public void setHelperErp(String helperErp) {
        this.helperErp = helperErp;
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

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
