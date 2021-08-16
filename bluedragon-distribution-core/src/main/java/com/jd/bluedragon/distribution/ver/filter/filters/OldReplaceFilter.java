package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dudong
 * @date 2016/3/2
 */
public class OldReplaceFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Rule rule1 = request.getRuleMap().get("1030");
        if (WaybillCacheHelper.isReplacement(request.getWaybillCache())
                && ! SiteHelper.matchSiteTypeRule(rule1.getContent(), request.getsReceiveSiteSubType())) {
            throw  new SortingCheckException(SortingResponse.CODE_29104,
                    HintService.getHintWithFuncModule(HintCodeConstants.OLD_FOR_NEW_WAYBILL, request.getFuncModule()));
        }

        chain.doFilter(request, chain);
    }
}
