package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 任务自动关闭服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
public interface JyBizTaskAutoCloseService {

    /**
     * 自动关闭任务
     * @param task 任务数据
     * @return 处理结果
     */
    boolean handleTimingCloseTask(Task task);
}
