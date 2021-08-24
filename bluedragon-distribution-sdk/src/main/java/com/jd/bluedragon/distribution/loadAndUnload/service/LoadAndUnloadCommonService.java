package com.jd.bluedragon.distribution.loadAndUnload.service;

import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * 转运装卸车拦截服务
 */
public interface LoadAndUnloadCommonService {

    /**
     * 卸车的拦截校验
     * @param barCode 包裹号
     * @return 校验结果
     */
    InvokeResult<String> interceptValidateUnloadCar(String barCode);

    /**
     * VER组板拦截校验
     * @param request 组板检查请求
     * @return 校验结果
     */
    InvokeResult<Void> boardCombinationCheck(BoardCommonRequest request);

}
