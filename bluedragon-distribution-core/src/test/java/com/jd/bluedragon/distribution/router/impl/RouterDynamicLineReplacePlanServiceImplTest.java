package com.jd.bluedragon.distribution.router.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.router.dynamicLine.enums.RouterDynamicLineStatusEnum;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;
import com.jd.bluedragon.distribution.router.IRouterDynamicLineReplacePlanService;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanMatchedEnableLineReq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 动态线路切换方案接口实现测试
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-04 00:00:38 周四
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
public class RouterDynamicLineReplacePlanServiceImplTest {

    @Resource
    private IRouterDynamicLineReplacePlanService routerDynamicLineReplacePlanService;

    /**
     * 消费路由消息服务
     */
    @Test
    public void consumeDynamicLineReplacePlanTest(){
        String mq = "{\n" +
                "    \"versionId\": 12,\n" +
                "    \"startNodeCode\": \"010F016\",\n" +
                "    \"oldEndNodeCode\": \"010F002\",\n" +
                "    \"newEndNodeCode\": \"010F017\",\n" +
                "    \"enableTime\": \"2024-04-01 10:05:22\",\n" +
                "    \"disableTime\": \"2024-04-03 10:05:22\",\n" +
                "    \"oldPlanLineCode\": \"L132\",\n" +
                "    \"oldPlanDepartureTime\": \"2024-04-01 10:05:22\",\n" +
                "    \"oldPlanFlowCode\": \"124234\",\n" +
                "    \"newPlanLineCode\": \"L133\",\n" +
                "    \"newPlanDepartureTime\": \"2024-04-01 10:05:22\",\n" +
                "    \"newPlanFlowCode\": \"A123423\",\n" +
                "    \"pushTime\": 1711944982600\n" +
                "}";
        DynamicLineReplacePlanMq dynamicLineReplacePlanMq = JsonHelper.fromJson(mq, DynamicLineReplacePlanMq.class);
        final Result<Boolean> result = routerDynamicLineReplacePlanService.consumeDynamicLineReplacePlan(dynamicLineReplacePlanMq);
        System.out.println(JsonHelper.toJson(result));
    }

    /**
     * 根据条件查询可用的动态线路替换方案列表
     *
     * @author fanggang7
     * @time 2024-04-03 17:45:38 周三
     */
    @Test
    public void getMatchedEnableLineTest(){
        RouterDynamicLineReplacePlanMatchedEnableLineReq req = new RouterDynamicLineReplacePlanMatchedEnableLineReq();
        req.setStartSiteId(40140);
        req.setOldEndSiteId(910);
        req.setNewEndSiteId(39);
        final Result<RouterDynamicLineReplacePlan> result = routerDynamicLineReplacePlanService.getMatchedEnableLine(req);
        System.out.println(JsonHelper.toJson(result));
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Test
    public void queryListByCondition4ClientTest(){
        RouterDynamicLineReplacePlanListReq req = new RouterDynamicLineReplacePlanListReq();
        final CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(40140);
        currentOperate.setSiteName("马驹桥分拣中心");
        req.setCurrentOperate(currentOperate);
        final User user = new User();
        user.setUserErp("fanggang7");
        user.setUserName("方刚");
        req.setUser(user);
        final Result<PageData<RouterDynamicLineReplacePlanVo>> pageDataJdCResponse = routerDynamicLineReplacePlanService.queryListByCondition4Client(req);
        System.out.println(JsonHelper.toJson(pageDataJdCResponse));
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Test
    public void changeStatus4ClientTest(){
        RouterDynamicLineReplacePlanChangeStatusReq req = new RouterDynamicLineReplacePlanChangeStatusReq();
        final CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(40140);
        currentOperate.setSiteName("马驹桥分拣中心");
        req.setCurrentOperate(currentOperate);
        final User user = new User();
        user.setUserErp("fanggang7");
        user.setUserName("方刚");
        req.setUser(user);
        req.setId(1L);
        req.setEnableStatus(RouterDynamicLineStatusEnum.ENABLE.getCode());
        routerDynamicLineReplacePlanService.changeStatus4Client(req);
    }
}
