package com.jd.bluedragon.distribution.router;

import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanMatchedEnableLineReq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;

/**
 * 动态线路切换方案接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-01 18:23:22 周一
 */
public interface IRouterDynamicLineReplacePlanService {

    /**
     * 消费路由消息服务
     * @param dynamicLineReplacePlanMq 路由消息实体
     * @return 处理结果
     */
    Result<Boolean> consumeDynamicLineReplacePlan(DynamicLineReplacePlanMq dynamicLineReplacePlanMq);

    /**
     * 根据条件查询可用的动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-03 17:45:38 周三
     */
    Result<RouterDynamicLineReplacePlan> getMatchedEnableLine(RouterDynamicLineReplacePlanMatchedEnableLineReq req);

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    Result<PageData<RouterDynamicLineReplacePlanVo>> queryListByCondition4Client(RouterDynamicLineReplacePlanListReq req);

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    Result<Boolean> changeStatus4Client(RouterDynamicLineReplacePlanChangeStatusReq req);
}
