package com.jd.bluedragon.distribution.businessIntercept.service;

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
public interface IBusinessInterceptExceptionTaskService {

    /**
     * 消费拦截报表数据
     * @return 消费结果
     * @author fanggang7
     * @time 2024-01-14 17:37:50 周日
     */
    Result<Boolean> consumeDmsBusinessInterceptReport(BusinessInterceptReport businessInterceptReport);

    /**
     * 消费拦截处理消息
     * @return 消费结果
     * @author fanggang7
     * @time 2024-01-14 17:38:09 周日
     */
    Result<Boolean> consumeDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord);
}
