package com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy;

import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 任务关闭接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
public interface JYBizTaskCloseService {

    /**
     * 关闭任务接口
     * @param autoCloseTaskPo 关闭入参
     * @return 处理结果
     * @author fanggang7
     * @time 2023-01-31 17:00:46 周二
     */
    Result<Void> closeTask(AutoCloseTaskPo autoCloseTaskPo);

}
