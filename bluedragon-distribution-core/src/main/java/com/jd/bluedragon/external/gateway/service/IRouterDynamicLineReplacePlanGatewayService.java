package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;

import java.util.List;

/**
 * 动态线路切换方案接口网关层
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-01 18:23:22 周一
 */
public interface IRouterDynamicLineReplacePlanGatewayService {

    /**
     * 根据条件查询动态线路替换方案列表
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    JdCResponse<List<RouterDynamicLineReplacePlanVo>> queryList(RouterDynamicLineReplacePlanListReq req);

    /**
     * 根据条件查询动态线路替换方案列表
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    JdCResponse<Boolean> changeStatus(RouterDynamicLineReplacePlanChangeStatusReq req);
}
