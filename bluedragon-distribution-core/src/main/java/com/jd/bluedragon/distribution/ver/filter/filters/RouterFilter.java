package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
    private WaybillCacheService waybillCacheService;

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

            //根据waybillCode查库获取路由信息
            String router = waybillCacheService.getRouterByWaybillCode(waybillCode);
            boolean verifyPass = false;
            List<Integer> routerShow = new ArrayList<>();

            boolean getCurNodeFlag = false;

            logger.info("RouterFilter根据运单号获取运单路由，运单号:" + waybillCode + "，路由：" + router);
            if (StringHelper.isNotEmpty(router)) {
                //路由校验逻辑
                String[] routerNodes = router.split(WAYBILL_ROUTER_SPLITER);
                for (int i = 0; i < routerNodes.length - 1; i++) {
                    int curNode = Integer.parseInt(routerNodes[i]);
                    int nexNode = Integer.parseInt(routerNodes[i + 1]);
                    if(curNode == createSiteCode){
                        getCurNodeFlag = true;
                        routerShow.add(nexNode);
                        if(nexNode == receiveSiteCode){
                            verifyPass = true;
                            break;
                        }
                    }
                }

                //路由中包括当前操作的分拣中心并且没有通过校验
                if(getCurNodeFlag && !verifyPass) {
                    //将下一站由编码转换成名称，并进行截取，供pda提示
                    StringBuilder routerShortNames= new StringBuilder();
                    for(Integer dmsCode : routerShow){
                        if(StringHelper.isEmpty(siteService.getDmsShortNameByCode(dmsCode))){
                            continue;
                        }
                        routerShortNames.append(siteService.getDmsShortNameByCode(dmsCode)).append(Constants.SEPARATOR_COMMA);
                    }
                    if(StringHelper.isNotEmpty(routerShortNames.toString())){
                        routerShortNames = new StringBuilder(routerShortNames.substring(0, routerShortNames.length() - 1));
                    }
                    throw new SortingCheckException(SortingResponse.CODE_CROUTER_ERROR,
                            SortingResponse.MESSAGE_CROUTER_ERROR + "路由下一站：" + routerShortNames);
                }
            }
        }

        chain.doFilter(request, chain);
    }
}
