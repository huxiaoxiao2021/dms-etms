package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.jsf.domain.ValidateIgnore;
import com.jd.bluedragon.distribution.jsf.domain.ValidateIgnoreRouterCondition;
import com.jd.bluedragon.distribution.jy.config.JYTransferSiteEntity;
import com.jd.bluedragon.distribution.jy.service.transfer.JyTransferConfigService;
import com.jd.bluedragon.distribution.jy.service.transfer.manager.JYTransferConfigProxy;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteMatchQo;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteQo;
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
    @Autowired
    private UccPropertyConfiguration uccConfiguration;

    @Autowired
    private JYTransferConfigProxy jyTransferConfigProxy;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        /* 判断如果是填航空仓订单则直接进行返回，不进行下面的下一跳校验 */
        if (WaybillCacheHelper.isAirWaybill(request.getWaybillCache())) {
            chain.doFilter(request,chain);
            return;
        }

        //发货目的地为德邦虚拟分拣中心的不校验
        List<Integer> dpSiteCodeList = uccConfiguration.getDpSiteCodeList();
        if(BusinessHelper.isDPSiteCode(dpSiteCodeList, request.getReceiveSiteCode())){
            chain.doFilter(request,chain);
            return;
        }


        // 德邦春节项目的错发校验跳过
        if (BusinessHelper.isDPWaybill1(request.getWaybillCache().getWaybillSign())) {
            ConfigTransferDpSiteMatchQo siteQo = new ConfigTransferDpSiteMatchQo();
            siteQo.setHandoverSiteCode(request.getCreateSiteCode());
            siteQo.setPreSortSiteCode(request.getWaybillSite().getCode());
            Result<ConfigTransferDpSite> result = jyTransferConfigProxy.queryMatchConditionRecord(siteQo);
            if (result.getData() != null && result.getData().getEffectiveStartTime().before(new Date()) && new Date().before(result.getData().getEffectiveStopTime())) {
                if (Objects.equals(result.getData().getHandoverSiteCode(), request.getCreateSiteCode()) && BusinessHelper.isDPSiteCode1(request.getReceiveSite().getCode(), request.getReceiveSite().getType(), request.getReceiveSite().getSubType())) {
                    chain.doFilter(request, chain);
                    return;
                }
                if (Objects.equals(result.getData().getHandoverSiteCode(), request.getCreateSiteCode()) && !BusinessHelper.isDPSiteCode1(request.getReceiveSite().getCode(), request.getReceiveSite().getType(), request.getReceiveSite().getSubType())) {
                    Map<String, String> hintParams = new HashMap<String, String>();
                    hintParams.put(HintArgsConstants.ARG_FIRST, request.getWaybillCode());
                    throw new SortingCheckException(Integer.valueOf(HintCodeConstants.JY_DP_TRANSFER_MESSAGE),
                            HintService.getHintWithFuncModule(HintCodeConstants.JY_DP_TRANSFER_MESSAGE, request.getFuncModule(), hintParams));
                }

            }

        }

        if (BusinessHelper.isDPSiteCode1(request.getReceiveSite().getCode(), request.getReceiveSite().getType(), request.getReceiveSite().getSubType())) {
            Map<String, String> hintParams = new HashMap<String, String>();
            hintParams.put(HintArgsConstants.ARG_FIRST, request.getWaybillCode());
            throw new SortingCheckException(Integer.valueOf(HintCodeConstants.JY_DP_TRANSFER_MESSAGE_1),
                    HintService.getHintWithFuncModule(HintCodeConstants.JY_DP_TRANSFER_MESSAGE_1, request.getFuncModule(), hintParams));
        }




        //加一个分拣规则
        Rule rule = null;
        try {
            rule = request.getRuleMap().get(RULE_ROUTER);
        } catch (Exception e) {
            logger.warn("站点 [" + request.getCreateSiteCode() + "] 类型 [" + RULE_ROUTER + "] 没有匹配的规则");
        }

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
                // 如果存在忽略校验，则继续走下一步
                final ValidateIgnore validateIgnore = request.getValidateIgnore();
                if(validateIgnore != null && validateIgnore.getValidateIgnoreRouterCondition() != null){
                    final ValidateIgnoreRouterCondition validateIgnoreCondition = validateIgnore.getValidateIgnoreRouterCondition();
                    final List<Long> receiveSiteIdList = validateIgnoreCondition.getReceiveSiteIdList();
                    if(CollectionUtils.isNotEmpty(validateIgnoreCondition.getReceiveSiteIdList()) && Objects.equals(validateIgnoreCondition.getMatchType(), ValidateIgnore.MATCH_TYPE_IN)){
                        if(routeNextDto.getFirstNextSiteId() != null && receiveSiteIdList.contains(Long.valueOf(routeNextDto.getFirstNextSiteId()))){
                            logger.info("RouterFilter validateIgnore: waybillCode: {} firstNextSiteId: {} receiveSiteIdList: {}", waybillCode, routeNextDto.getFirstNextSiteId(), receiveSiteIdList);
                            chain.doFilter(request, chain);
                            return;
                        }
                    }
                }
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
