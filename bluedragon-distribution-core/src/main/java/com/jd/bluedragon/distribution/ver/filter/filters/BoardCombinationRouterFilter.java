package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created by shipeilin on 2018/6/21.
 *  此filter功能组板检验，下一站是分拣中心则通过路由下一跳和组板目的地是否一致校验
 *  下一站是站点则通过预分拣站点与组板目的地是否一致校验
 *  注意：大件没有预分拣站点，需要剔除大件过此校验链
 */
public class BoardCombinationRouterFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String WAYBILL_ROUTER_SPLITER = "\\|";
    private static final String RULE_ROUTER = "1126";
    private static final String SWITCH_ON = "1";

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private SiteService siteService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private RouterService routerService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("do filter process...");

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

            if(SiteHelper.isDistributionCenter(request.getReceiveSite())){
                //根据waybillCode查库获取路由信息

                BaseStaffSiteOrgDto routeNextDto = routerService.getRouterNextSite(createSiteCode, waybillCode);

                //路由中包括当前操作的分拣中心并且没有通过校验
                if (routeNextDto != null && !Objects.equals(routeNextDto.getSiteCode(), receiveSiteCode)) {
                    //将下一站由编码转换成名称，并进行截取，供pda提示
                    String routerShortNames = routeNextDto.getSiteName()
                            .replace(Constants.SUFFIX_DMS_ONE,"")
                            .replace(Constants.SUFFIX_DMS_TWO,"")
                            .replace(Constants.SUFFIX_TRANSIT,"");
                    throw new SortingCheckException(SortingResponse.CODE_CROUTER_ERROR,
                            SortingResponse.MESSAGE_BOARD_ROUTER_ERROR + "路由下一站：" + routerShortNames);
                } else if (routeNextDto == null && uccPropertyConfiguration.isControlCheckRoute()){
                    throw new SortingCheckException(SortingResponse.CODE_CROUTER_ERROR,
                            SortingResponse.MESSAGE_BOARD_ROUTER_EMPTY_ERROR);
                }

            }
            if(SiteHelper.isDelivery(request.getReceiveSite())&&!WaybillUtil.isLasWaybillCode(waybillCode)){
                //下一站是站点的情况
                //获取预分拣站点
                Integer siteCode = request.getWaybillCache().getSiteCode();
                //获取反调度站点
                if (siteCode == null) {
                    siteCode = siteService.getLastScheduleSite(request.getWaybillCache().getWaybillCode());
                }
                if(!siteCode.equals(receiveSiteCode)){
                    throw new SortingCheckException(SortingResponse.CODE_CROUTER_ERROR,SortingResponse.MESSAGE_BOARD_ERROR);
                }
            }

        }

        chain.doFilter(request, chain);
    }
}
