package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by xumei3 on 2018/3/21.
 */
public class RouterFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String WAYBILL_ROUTER_SPLITER = "\\|";
    private static final String RULE_ROUTER = "1122";
    private static final String SWITCH_ON = "1";

    @Autowired
    private SiteService siteService;

    @Autowired
    private RouterService routerService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("分拣校验RouterFilter1packageCode[{}]pdaOperateRequest[{}]",request.getPackageCode(), JsonHelper.toJson(request));
        /* 判断如果是填航空仓订单则直接进行返回，不进行下面的下一跳校验 */
        if (WaybillCacheHelper.isAirWaybill(request.getWaybillCache())) {
            chain.doFilter(request,chain);
            return;
        }

        //加一个分拣规则
        Rule rule = null;
        try {
            rule = request.getRuleMap().get(RULE_ROUTER);
        } catch (Exception e) {
            logger.warn("站点 [" + request.getCreateSiteCode() + "] 类型 [" + RULE_ROUTER + "] 没有匹配的规则");
        }
        logger.info("分拣校验RouterFilter2packageCode[{}]pdaOperateRequest[{}]rule[{}]",request.getPackageCode(), JsonHelper.toJson(request), JsonHelper.toJson(request));
        //规则没有配，或者配置了内容不等于1才会进行校验
        if (rule == null || !SWITCH_ON.equals(rule.getContent())) {
            Integer createSiteCode = request.getCreateSiteCode();
            Integer receiveSiteCode = request.getReceiveSiteCode();

            String waybillCode =request.getWaybillCode();
            if(StringHelper.isEmpty(waybillCode) && request.getWaybillCache()!=null){
                waybillCode = request.getWaybillCache().getWaybillCode();
            }
            RouteNextDto routeNextDto = routerService.matchRouterNextNode(createSiteCode,waybillCode);

            logger.info("RouterFilter根据运单号获取运单路由，运单号[{}]routExistCurrentSite[{}]firstNextSiteId[{}]",
                    waybillCode,routeNextDto.isRoutExistCurrentSite(),routeNextDto.getFirstNextSiteId());
            if(routeNextDto.isRoutExistCurrentSite() &&
                    !isRightReceiveSite(receiveSiteCode, routeNextDto)) {
                String siteName = siteService.getDmsShortNameByCode(routeNextDto.getFirstNextSiteId());
                Map<String, String> argsMap = new HashMap<>();
                argsMap.put(HintArgsConstants.ARG_FIRST, siteName);
                throw new SortingCheckException(SortingResponse.CODE_CROUTER_ERROR,
                        HintService.getHintWithFuncModule(HintCodeConstants.BATCH_DEST_AND_NEXT_ROUTER_DIFFERENCE, request.getFuncModule(), argsMap));
            }

        }

        chain.doFilter(request, chain);
    }

    private boolean isRightReceiveSite(Integer receiveSiteCode, RouteNextDto routeNextDto) {
        return CollectionUtils.isNotEmpty(routeNextDto.getNextSiteIdList())
                && Objects.equals(routeNextDto.getFirstNextSiteId(),receiveSiteCode);
    }
}
