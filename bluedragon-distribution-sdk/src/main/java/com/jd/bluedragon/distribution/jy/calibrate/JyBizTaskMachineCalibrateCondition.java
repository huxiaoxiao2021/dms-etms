package com.jd.bluedragon.distribution.jy.calibrate;

import java.util.Date;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/12/13 6:16 PM
 */
public class JyBizTaskMachineCalibrateCondition extends JyBizTaskMachineCalibrateDetailEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 查询开始时间
     */
    private Date queryStartTime;
    /**
     * 查询结束时间
     */
    private Date queryEndTime;

    /**
     * 体积或者重量校准的操作时间
     */
    private Date calibrateTime;
    /**
     * 任务状态
     */
    private List<Integer> taskStatusList;

    /**
     * 分页参数-开始值
     */
    private int offset = 0;
    /**
     * 分页参数-数据条数
     */
    private int limit = 10;

    private Integer pageNumber;

    private Integer pageSize;

    public Date getQueryStartTime() {
        return queryStartTime;
    }

    public void setQueryStartTime(Date queryStartTime) {
        this.queryStartTime = queryStartTime;
    }

    public Date getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(Date queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public Date getCalibrateTime() {
        return calibrateTime;
    }

    public void setCalibrateTime(Date calibrateTime) {
        this.calibrateTime = calibrateTime;
    }

    public List<Integer> getTaskStatusList() {
        return taskStatusList;
    }

    public void setTaskStatusList(List<Integer> taskStatusList) {
        this.taskStatusList = taskStatusList;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
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
}
