package com.jd.bluedragon.distribution.router.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.router.dynamicLine.enums.RouterDynamicLineStatusEnum;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanListReq;
import com.jd.bluedragon.common.dto.router.dynamicLine.response.RouterDynamicLineReplacePlanVo;
import com.jd.bluedragon.distribution.router.IRouterDynamicLineReplacePlanService;
import com.jd.bluedragon.distribution.router.dto.DynamicLineReplacePlanMq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanMatchedEnableLineReq;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.bluedragon.external.gateway.service.IRouterDynamicLineReplacePlanGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.PageData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * 动态线路切换方案网关接口实现测试
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-04 00:00:38 周四
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-context-dev.xml")
public class RouterDynamicLineReplacePlanGatewayServiceImplTest {

    @Resource
    private IRouterDynamicLineReplacePlanGatewayService routerDynamicLineReplacePlanGatewayService;

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Test
    public void queryListByCondition4Client(){
        RouterDynamicLineReplacePlanListReq req = new RouterDynamicLineReplacePlanListReq();
        final CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(40140);
        currentOperate.setSiteName("马驹桥分拣中心");
        req.setCurrentOperate(currentOperate);
        final User user = new User();
        user.setUserErp("fanggang7");
        user.setUserName("方刚");
        req.setUser(user);
        final JdCResponse<PageData<RouterDynamicLineReplacePlanVo>> pageDataJdCResponse = routerDynamicLineReplacePlanGatewayService.queryList(req);
        System.out.println(JsonHelper.toJson(pageDataJdCResponse));
    }

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Test
    public void changeStatus4Client(){
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
        routerDynamicLineReplacePlanGatewayService.changeStatus(req);
    }
}
