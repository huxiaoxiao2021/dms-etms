package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 异常任务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-22 16:06:13 周一
 */
public interface JyExceptionTaskService<T> {

    /**
     * 添加任务
     * @return
     * @author fanggang7
     * @time 2024-01-22 16:07:04 周一
     */
    Result<Boolean> getBizId(T t);

    /**
     * 添加任务
     * @return
     * @author fanggang7
     * @time 2024-01-22 16:07:04 周一
     */
    Result<Boolean> addTask();

    /**
     * 添加任务
     * @return
     * @author fanggang7
     * @time 2024-01-22 16:07:04 周一
     */
    Result<Boolean> addTaskAndDetail();

}
