package com.jd.bluedragon.distribution.router.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/8/31 16:33
 */
@Service
public class RouterServiceImpl implements RouterService {

    private Logger log = LoggerFactory.getLogger(RouterServiceImpl.class);

    @Autowired
    private SiteService siteService;

    @Autowired
    private WaybillCacheService waybillCacheService;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final  String WAYBILL_ROUTER_SPLITER = "\\|";

    @Override
    public BaseStaffSiteOrgDto getRouterNextSite(Integer siteCode, String waybillCode) {

        if(!WaybillUtil.isWaybillCode(waybillCode)){
            return null;
        }
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.RouterServiceImpl.getRouterNextSite");
        try{

            RouteNextDto routeNextDto = this.matchRouterNextNode(siteCode,waybillCode);
            if(routeNextDto.getFirstNextSiteId() == null){
                return null;
            }
            return siteService.getSite(routeNextDto.getFirstNextSiteId());
        }catch (Exception e){
            Profiler.functionError(info);
            this.log.error("通过运单号:{} 查询站点:{}的路由下一节点失败!",waybillCode,siteCode,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.RouterServiceImpl.matchRouterNextNode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public RouteNextDto matchRouterNextNode(Integer siteCode, String waybillCode){
        if(siteCode == null || StringUtils.isEmpty(waybillCode)){
            return RouteNextDto.NONE;
        }
        String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        if(StringUtils.isEmpty(routerStr)){
            return RouteNextDto.NONE;
        }
        String[] routers = routerStr.split(WAYBILL_ROUTER_SPLITER);
        List<Integer> nextSiteIdList = Lists.newArrayList();
        Integer firstNextSiteId = null;
        int arrayLastIndex = routers.length - 1;
        for (int i = arrayLastIndex; i >= 0; i--) {
            Integer item = NumberHelper.convertToInteger(routers[i]);
            if(Objects.equals(siteCode,item)){
                return new RouteNextDto(firstNextSiteId,Boolean.TRUE,nextSiteIdList, null);
            }
            nextSiteIdList.add(0,item);
            firstNextSiteId = item;
        }
        return RouteNextDto.NONE;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.RouterServiceImpl.matchNextNodeByRouter", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public RouteNextDto matchNextNodeAndLastNodeByRouter(Integer siteCode, String waybillCode, String routerStr){
        if(siteCode == null || StringUtils.isEmpty(waybillCode)){
            return RouteNextDto.NONE;
        }
        if(StringUtils.isEmpty(routerStr)) {
            routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        }
        if(StringUtils.isEmpty(routerStr)){
            return RouteNextDto.NONE;
        }
        String[] routers = routerStr.split(WAYBILL_ROUTER_SPLITER);
        List<Integer> nextSiteIdList = Lists.newArrayList();
        Integer firstNextSiteId = null;
        Integer firstLastSiteId = null;
        int arrayLastIndex = routers.length - 1;

        boolean currentSite = false;
        for (int i = arrayLastIndex; i >= 0; i--) {
            Integer item = NumberHelper.convertToInteger(routers[i]);
            if(Objects.equals(siteCode,item)){
                currentSite = true;
                continue;
            }

            if(!currentSite) {
                nextSiteIdList.add(0,item);
                firstNextSiteId = item;
            }else {
                firstLastSiteId = item;
                break;
            }
        }
        if(!currentSite) {
            return RouteNextDto.NONE;
        }
        return new RouteNextDto(firstNextSiteId,Boolean.TRUE,nextSiteIdList, firstLastSiteId);
    }
}
