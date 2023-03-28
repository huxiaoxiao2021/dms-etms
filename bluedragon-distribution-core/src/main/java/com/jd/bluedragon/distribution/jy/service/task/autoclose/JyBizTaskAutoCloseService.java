package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 任务自动关闭服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
public interface JyBizTaskAutoCloseService {

    /**
     * 消费自动关闭消息
     * @return 消费结果
     * @author fanggang7
     * @time 2023-03-28 15:50:49 周二
     */
    Result<Boolean> consumeJyBizTaskAutoCloseMq(AutoCloseTaskMq autoCloseTaskMq);

    /**
     * 自动关闭任务
     * @param task 任务数据
     * @return 处理结果
     */
    boolean handleTimingCloseTask(Task task);

}
