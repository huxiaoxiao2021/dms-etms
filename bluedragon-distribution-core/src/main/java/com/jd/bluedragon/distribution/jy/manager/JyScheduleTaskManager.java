package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.jy.schedule.base.ServiceResult;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;

import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/6
 * @Description:
 */
public interface JyScheduleTaskManager {

    /**
     * 创建调度任务
     * @param req
     * @return
     */
    JyScheduleTaskResp createScheduleTask(JyScheduleTaskReq req);

    /**
     * 关闭调度任务
     * @param req
     * @return
     */
    JyScheduleTaskResp closeScheduleTask(JyScheduleTaskReq req);

    /**
     * 分配调度任务
     * @param req
     * @return
     */
    JyScheduleTaskResp distributeAndStartScheduleTask(JyScheduleTaskReq req);

    /**
     * 通过业务主键和任务类型获取调度任务
     * @param req
     * @return
     */
    JyScheduleTaskResp findScheduleTaskByBizId(JyScheduleTaskReq req);

    /**
     * 根据分配目标获取当前已开始的任务信息
     * @param req
     * @return
     */
    List<JyScheduleTaskResp> findStartedScheduleTasksByDistribute(JyScheduleTaskReq req);
    /**
     * 根据分配目标获取当前已开始的任务信息（添加组员）
     * @param req
     * @return
     */
    List<JyScheduleTaskResp> findStartedScheduleTasksForAddMember(JyScheduleTaskReq req);    
}
