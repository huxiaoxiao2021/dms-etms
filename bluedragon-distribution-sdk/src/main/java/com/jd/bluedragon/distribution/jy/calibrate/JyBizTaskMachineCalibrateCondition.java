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
     * 任务状态
     */
    private List<Integer> taskStatusList;

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

    public List<Integer> getTaskStatusList() {
        return taskStatusList;
    }

    public void setTaskStatusList(List<Integer> taskStatusList) {
        this.taskStatusList = taskStatusList;
    }
}
