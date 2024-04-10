package com.jd.bluedragon.distribution.router.manager.impl;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.router.RouterDynamicLineReplacePlanJsfService;
import com.jd.bd.dms.automatic.sdk.modules.router.dto.RouterDynamicLineReplacePlanDto;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.router.dynamicLine.request.RouterDynamicLineReplacePlanChangeStatusReq;
import com.jd.bluedragon.distribution.router.dao.RouterDynamicLineReplacePlanDao;
import com.jd.bluedragon.distribution.router.dao.RouterDynamicLineReplacePlanLogDao;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlan;
import com.jd.bluedragon.distribution.router.domain.RouterDynamicLineReplacePlanLog;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanChangeStatusDto;
import com.jd.bluedragon.distribution.router.dto.request.RouterDynamicLineReplacePlanQuery;
import com.jd.bluedragon.distribution.router.manager.IRouterDynamicLineReplacePlanManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@Slf4j
@Component("routerDynamicLineReplacePlanManager")
public class RouterDynamicLineReplacePlanManagerImpl implements IRouterDynamicLineReplacePlanManager {

    @Autowired
    private RouterDynamicLineReplacePlanDao routerDynamicLineReplacePlanDao;

    @Autowired
    private RouterDynamicLineReplacePlanLogDao routerDynamicLineReplacePlanLogDao;

    @Autowired
    private RouterDynamicLineReplacePlanJsfService routerDynamicLineReplacePlanJsfService;

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
        final RouterDynamicLineReplacePlanChangeStatusDto routerDynamicLineReplacePlan = getRouterDynamicLineReplacePlanChangeStatusDto(req, routerDynamicLineReplacePlanExist, currentTime, user);
        final int updateCount = routerDynamicLineReplacePlanDao.updatesStatusByPrimaryKeySelective(routerDynamicLineReplacePlan);
        if(updateCount > 0){
            // step 同步数据至自动化系统
            RouterDynamicLineReplacePlanQuery conditon = new RouterDynamicLineReplacePlanQuery();
            conditon.setId(routerDynamicLineReplacePlan.getId());
            conditon.setStartSiteId(routerDynamicLineReplacePlan.getStartSiteId());
            RouterDynamicLineReplacePlan source = routerDynamicLineReplacePlanDao.selectOne(conditon);
            RouterDynamicLineReplacePlanDto dest = new RouterDynamicLineReplacePlanDto();
            BeanUtils.copyProperties(source,dest);
            BaseDmsAutoJsfResponse<Boolean> response = routerDynamicLineReplacePlanJsfService.syncRouterLine(dest);
            if (response == null || !response.getData()){
                log.warn("同步路由数据至自动化系统失败，提示用户重试！");
                throw new RuntimeException("同步路由数据至自动化系统失败，提示用户重试！");
            }

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
        }
        return true;
    }

    private RouterDynamicLineReplacePlanChangeStatusDto getRouterDynamicLineReplacePlanChangeStatusDto(RouterDynamicLineReplacePlanChangeStatusReq req, RouterDynamicLineReplacePlan routerDynamicLineReplacePlanExist, Date currentTime, User user) {
        final RouterDynamicLineReplacePlanChangeStatusDto routerDynamicLineReplacePlan = new RouterDynamicLineReplacePlanChangeStatusDto();
        routerDynamicLineReplacePlan.setId(req.getId());
        routerDynamicLineReplacePlan.setEnableStatus(req.getEnableStatus());
        routerDynamicLineReplacePlan.setEnableStatusPrev(routerDynamicLineReplacePlanExist.getEnableStatus());
        routerDynamicLineReplacePlan.setUpdateTime(currentTime);
        routerDynamicLineReplacePlan.setUpdateUserId(user.getUserCode());
        routerDynamicLineReplacePlan.setUpdateUserCode(user.getUserErp());
        routerDynamicLineReplacePlan.setUpdateUserName(user.getUserName());
        return routerDynamicLineReplacePlan;
    }
}
