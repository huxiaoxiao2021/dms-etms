package com.jd.bluedragon.distribution.router.dao;

import com.jd.bluedragon.common.dto.router.dynamicLine.enums.RouterDynamicLineStatusEnum;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 动态线路切换方案dao测试
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-03 23:54:51 周三
 */
public class RouterDynamicLineReplacePlanDaoTest extends AbstractDaoIntegrationTest {

    @Autowired
    private RouterDynamicLineReplacePlanDao routerDynamicLineReplacePlanDao;

    @Test
    public void queryCountTest() {
        final RouterDynamicLineReplacePlanQuery query = new RouterDynamicLineReplacePlanQuery();
        query.setStartSiteId(40240);
        query.setNewEndSiteId(910);
        query.setEnableStatusList(new ArrayList<>(Arrays.asList(RouterDynamicLineStatusEnum.ENABLE.getCode(), RouterDynamicLineStatusEnum.DEFAULT.getCode())));
        final Long result = routerDynamicLineReplacePlanDao.queryCount(query);
        System.out.println(JsonHelper.toJson(result));
    }

    @Test
    public void queryListTest() {
        final RouterDynamicLineReplacePlanQuery query = new RouterDynamicLineReplacePlanQuery();
        query.setStartSiteId(40240);
        query.setNewEndSiteId(910);
        query.setEnableStatusList(new ArrayList<>(Arrays.asList(RouterDynamicLineStatusEnum.ENABLE.getCode(), RouterDynamicLineStatusEnum.DEFAULT.getCode())));
        final List<RouterDynamicLineReplacePlan> result = routerDynamicLineReplacePlanDao.queryList(query);
        System.out.println(JsonHelper.toJson(result));
    }

    @Test
    public void updateByPrimaryKeySelectiveTest() {
        final RouterDynamicLineReplacePlan update = new RouterDynamicLineReplacePlan();
        update.setId(1L);
        update.setStartSiteId(40240);
        update.setNewEndSiteId(910);
        update.setUpdateUserId(1234);
        update.setUpdateUserCode("fanggang7");
        update.setUpdateUserName("方刚");
        int i = routerDynamicLineReplacePlanDao.updateByPrimaryKeySelective(update);
        System.out.println(i);
    }

    @Test
    public void selectLatestOneText() {
        final RouterDynamicLineReplacePlanQuery query = new RouterDynamicLineReplacePlanQuery();
        query.setStartSiteId(40240);
        query.setNewEndSiteId(910);
        query.setEnableStatusList(new ArrayList<>(Arrays.asList(RouterDynamicLineStatusEnum.ENABLE.getCode(), RouterDynamicLineStatusEnum.DEFAULT.getCode())));
        final RouterDynamicLineReplacePlan result = routerDynamicLineReplacePlanDao.selectLatestOne(query);
        System.out.println(JsonHelper.toJson(result));
    }
}