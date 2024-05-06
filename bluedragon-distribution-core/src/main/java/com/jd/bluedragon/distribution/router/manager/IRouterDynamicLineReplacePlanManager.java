package com.jd.bluedragon.distribution.router.manager;

import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.dms.java.utils.sdk.base.Result;

/**
 * 动态线路切换方案manager层
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-07 10:43:55 周日
 */
public interface IRouterDynamicLineReplacePlanManager {

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    boolean changeStatus4Client(RouterDynamicLineReplacePlanChangeStatusReq req, RouterDynamicLineReplacePlan routerDynamicLineReplacePlanExist);
}
