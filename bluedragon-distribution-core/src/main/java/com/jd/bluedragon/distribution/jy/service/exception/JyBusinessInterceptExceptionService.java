package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptDisposeRecord;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 拦截异常任务相关接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-14 17:30:22 周日
 */
public interface JyBusinessInterceptExceptionService {

    /**
     * 增加拦截异常任务
     * @param businessInterceptReport 拦截记录
     * @return 处理结果
     * @time 2024-01-14 17:37:50 周日
     */
    Result<Boolean> addInterceptTask(BusinessInterceptReport businessInterceptReport);

    /**
     * 处理异常任务
     * @return 处理结果
     * @author fanggang7
     * @time 2024-01-14 17:38:09 周日
     */
    Result<Boolean> disposeInterceptTask(BusinessInterceptDisposeRecord businessInterceptDisposeRecord);
}
