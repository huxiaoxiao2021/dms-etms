package com.jd.bluedragon.distribution.router.impl;

import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;
import com.jd.bluedragon.distribution.router.IRouterDynamicLineReplacePlanService;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.dms.java.utils.sdk.base.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 动态线路切换方案接口实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-01 18:23:22 周一
 */
@Service("routerDynamicLineReplacePlanService")
public class RouterDynamicLineReplacePlanServiceImpl implements IRouterDynamicLineReplacePlanService {

    /**
     * 消费路由消息服务
     *
     * @param dynamicLineReplacePlanMq 路由消息实体
     * @return 处理结果
     */
    @Override
    public Result<Boolean> consumeDynamicLineReplacePlan(DynamicLineReplacePlanMq dynamicLineReplacePlanMq) {
        return null;
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
    public Result<List<RouterDynamicLineReplacePlan>> queryListByCondition(RouterDynamicLineReplacePlanQuery req) {
        return null;
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
    public Result<List<RouterDynamicLineReplacePlanVo>> queryListByCondition4Client(RouterDynamicLineReplacePlanListReq req) {
        return null;
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
    public Result<Boolean> changeStatus4Client(RouterDynamicLineReplacePlanChangeStatusReq req) {
        return null;
    }
}
