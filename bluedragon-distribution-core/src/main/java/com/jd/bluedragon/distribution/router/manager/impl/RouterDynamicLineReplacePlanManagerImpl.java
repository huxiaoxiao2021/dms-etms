package com.jd.bluedragon.distribution.router.manager.impl;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.distribution.router.dao.RouterDynamicLineReplacePlanDao;
import com.jd.bluedragon.distribution.router.dao.RouterDynamicLineReplacePlanLogDao;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlanLog;
import com.jd.bluedragon.distribution.router.manager.IRouterDynamicLineReplacePlanManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 动态线路切换方案manager实现层
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-07 10:43:55 周日
 */
@Component("routerDynamicLineReplacePlanManager")
public class RouterDynamicLineReplacePlanManagerImpl implements IRouterDynamicLineReplacePlanManager {

    @Autowired
    private RouterDynamicLineReplacePlanDao routerDynamicLineReplacePlanDao;

    @Autowired
    private RouterDynamicLineReplacePlanLogDao routerDynamicLineReplacePlanLogDao;

    /**
     * 根据条件查询动态线路替换方案列表
     *
     * @param req 请求入参
     * @return 数据列表
     * @author fanggang7
     * @time 2024-04-02 10:53:44 周二
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean changeStatus4Client(RouterDynamicLineReplacePlanChangeStatusReq req, RouterDynamicLineReplacePlan routerDynamicLineReplacePlanExist) {
        final User user = req.getUser();
        Date currentTime = new Date();
        // step 更新数据
        final RouterDynamicLineReplacePlan routerDynamicLineReplacePlan = getRouterDynamicLineReplacePlan(req, currentTime, user);
        routerDynamicLineReplacePlanDao.updateByPrimaryKeySelective(routerDynamicLineReplacePlan);

        // step 写入日志
        final RouterDynamicLineReplacePlanLog routerDynamicLineReplacePlanLog = new RouterDynamicLineReplacePlanLog();
        routerDynamicLineReplacePlanLog.setRefId(req.getId());
        routerDynamicLineReplacePlanLog.setStatusPrev(routerDynamicLineReplacePlanExist.getEnableStatus());
        routerDynamicLineReplacePlanLog.setStatusTarget(req.getEnableStatus());
        routerDynamicLineReplacePlanLog.setCreateTime(currentTime);
        routerDynamicLineReplacePlanLog.setOperateTime(currentTime);
        routerDynamicLineReplacePlanLog.setCreateUserId(user.getUserCode());
        routerDynamicLineReplacePlanLog.setCreateUserErp(user.getUserErp());
        routerDynamicLineReplacePlanLog.setCreateUserName(user.getUserName());
        routerDynamicLineReplacePlanLogDao.insertSelective(routerDynamicLineReplacePlanLog);

        return false;
    }

    private RouterDynamicLineReplacePlan getRouterDynamicLineReplacePlan(RouterDynamicLineReplacePlanChangeStatusReq req, Date currentTime, User user) {
        final RouterDynamicLineReplacePlan routerDynamicLineReplacePlan = new RouterDynamicLineReplacePlan();
        routerDynamicLineReplacePlan.setId(req.getId());
        routerDynamicLineReplacePlan.setEnableStatus(req.getEnableStatus());
        routerDynamicLineReplacePlan.setUpdateTime(currentTime);
        routerDynamicLineReplacePlan.setUpdateUserId(user.getUserCode());
        routerDynamicLineReplacePlan.setUpdateUserCode(user.getUserErp());
        routerDynamicLineReplacePlan.setUpdateUserName(user.getUserName());
        return routerDynamicLineReplacePlan;
    }
}
