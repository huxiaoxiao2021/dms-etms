package com.jd.bluedragon.distribution.router.impl;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private JsfSortingResourceService jsfSortingResourceService;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final  String WAYBILL_ROUTER_SPLITER = "\\|";

    @Override
    public BaseStaffSiteOrgDto getRouterNextSite(Integer siteCode, String waybillCode) {

        if(!WaybillUtil.isWaybillCode(waybillCode)){
            return null;
        }
        Integer nextRouterSiteCode = null;
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.RouterServiceImpl.getRouterNextSite");
        try{
            String routerStr = jsfSortingResourceService.getRouterByWaybillCode(waybillCode);

            if(StringUtils.isNotBlank(routerStr)){
                String[] routers = routerStr.split(WAYBILL_ROUTER_SPLITER);
                if(routers != null && routers.length > 0) {
                    for (int i = 0; i < routers.length - 1; i++) {
                        if(siteCode.equals(Integer.valueOf(routers[i]))){
                            nextRouterSiteCode = Integer.valueOf(routers[i+1]);
                            break;
                        }
                    }
                }
            }
            return siteService.getSite(nextRouterSiteCode);
        }catch (Exception e){
            Profiler.functionError(info);
            this.log.error("通过运单号:{} 查询站点:{}的路由下一节点失败!",waybillCode,siteCode,e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }
}
