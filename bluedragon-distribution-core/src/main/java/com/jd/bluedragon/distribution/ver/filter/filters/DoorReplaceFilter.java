package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.common.domain.WaybillCache;
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
public class DoorReplaceFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
      /*  if(logger.isInfoEnabled()){*/
            logger.info("do filter process...");
        /*}*/
        Rule rule1 = request.getRuleMap().get("1040");
        if (WaybillCacheHelper.isDoorReplacement(request.getWaybillCache())
                && !SiteHelper.matchSiteTypeRule(rule1.getContent(), request.getsReceiveSiteSubType())) {
            throw new SortingCheckException(SortingResponse.CODE_29103,
                    SortingResponse.MESSAGE_29103);
        }

        chain.doFilter(request, chain);
    }
}
