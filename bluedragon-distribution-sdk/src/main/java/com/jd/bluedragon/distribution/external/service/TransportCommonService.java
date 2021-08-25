package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.TransportServiceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * 转运依赖分拣相关服务
 */
public interface TransportCommonService {

    /**
     * 卸车的拦截校验
     */
    InvokeResult<Boolean> interceptValidateUnloadCar(TransportServiceRequest transportServiceRequest);

    /**
     * VER组板拦截校验
     * @param request 组板检查请求
     * @return 校验结果
     */
    InvokeResult<Void> boardCombinationCheck(BoardCommonRequest request);

    /**
     * 判断PDA登录ERP或登录ERP所属场地是否有配置验货/发货白名单
     * @param transportServiceRequest
     * @return
     */
    InvokeResult<Boolean> hasInspectOrSendFunction(TransportServiceRequest transportServiceRequest);

    /**
     * 获取路由下一场地编码
     * @param transportServiceRequest
     * @return
     */
    InvokeResult<Integer> getRouterNextSiteId(TransportServiceRequest transportServiceRequest);
}
