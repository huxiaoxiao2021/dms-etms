package com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept;

import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetailKv;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 异常任务-拦截异常明细关系服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-19 16:11:46 周五
 */
public interface JyExceptionBusinessInterceptDetailKvService {

    /**
     * 根据关键字查询最后一条记录
     * @param keyword 关键字
     * @return 明细结果
     * @author fanggang7
     * @time 2024-01-19 16:14:57 周五
     */
    Result<JyExceptionInterceptDetailKv> getLastOneByKeyword(String keyword);

    /**
     * 插入一个不重复的数据
     * @param jyExceptionInterceptDetailKv 待插入数据
     * @return 明细结果
     * @author fanggang7
     * @time 2024-01-19 16:14:57 周五
     */
    Result<Integer> addOneNoRepeat(JyExceptionInterceptDetailKv jyExceptionInterceptDetailKv);
}
