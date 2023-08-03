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

    /**
     * PDA选择不同的异常类型、破损类型、修复类型进行判断
     * @param req
     * @return
     */
    public abstract JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req);

}
