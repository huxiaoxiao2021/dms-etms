package com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept;

import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetail;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailQuery;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 异常任务-拦截异常明细服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-19 16:11:46 周五
 */
public interface JyExceptionBusinessInterceptDetailService {

    /**
     * 根据条件查询一个明细
     * @param jyExceptionInterceptDetailQuery 查询入参
     * @return 明细结果
     * @author fanggang7
     * @time 2024-01-19 16:14:57 周五
     */
    Result<JyExceptionInterceptDetail> selectOne(JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery);
}
