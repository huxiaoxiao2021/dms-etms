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
     * 校准任务明细状态:0-待处理,1-已完成,2-超时,3-关闭
     */
    private Integer calibrateTaskStatus;

    /**
     * 任务状态
     */
    private List<Integer> taskStatusList;

    /**
     * 分页参数-开始值
     */
    private int offSet = 0;
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

    public Integer getCalibrateTaskStatus() {
        return calibrateTaskStatus;
    }

    public void setCalibrateTaskStatus(Integer calibrateTaskStatus) {
        this.calibrateTaskStatus = calibrateTaskStatus;
    }

    public List<Integer> getTaskStatusList() {
        return taskStatusList;
    }

    public void setTaskStatusList(List<Integer> taskStatusList) {
        this.taskStatusList = taskStatusList;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
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
