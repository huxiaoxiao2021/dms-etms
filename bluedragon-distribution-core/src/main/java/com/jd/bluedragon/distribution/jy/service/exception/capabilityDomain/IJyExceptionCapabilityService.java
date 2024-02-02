package com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain;

import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 异常岗能力域
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-19 14:18:04 周五
 */
public interface IJyExceptionCapabilityService<T> {

    Result<Boolean> addTask(JyBizTaskExceptionEntity jyBizTaskExceptionEntity);

    Result<Boolean> addTaskAndDetail(JyBizTaskExceptionEntity jyBizTaskExceptionEntity, T t);
}
