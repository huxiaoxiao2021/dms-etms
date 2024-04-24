package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;
import com.jd.bluedragon.distribution.router.IRouterDynamicLineReplacePlanService;
import com.jd.bluedragon.external.gateway.service.IRouterDynamicLineReplacePlanGatewayService;
import com.jd.bluedragon.utils.converter.ResultConverter;
import com.jd.dms.java.utils.sdk.base.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 动态线路切换方案接口网关层实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-01 18:23:22 周一
 */
@Service
public class RouterDynamicLineReplacePlanGatewayServiceImpl implements IRouterDynamicLineReplacePlanGatewayService {

    @Autowired
    private IRouterDynamicLineReplacePlanService routerDynamicLineReplacePlanService;

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Override
    public JdCResponse<PageData<RouterDynamicLineReplacePlanVo>> queryList(RouterDynamicLineReplacePlanListReq req) {
        return ResultConverter.convertResultToJdcResponse(routerDynamicLineReplacePlanService.queryListByCondition4Client(req));
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Override
    public JdCResponse<Boolean> changeStatus(RouterDynamicLineReplacePlanChangeStatusReq req) {
        return ResultConverter.convertResultToJdcResponse(routerDynamicLineReplacePlanService.changeStatus4Client(req));
    }
}
