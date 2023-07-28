package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:18
 * @Description: 异常策略类
 */
public abstract class JyExceptionStrategy {

    public abstract Integer getExceptionType();


    public abstract JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req);

}
