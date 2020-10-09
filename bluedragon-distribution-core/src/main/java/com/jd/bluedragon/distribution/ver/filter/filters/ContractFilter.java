package com.jd.bluedragon.distribution.ver.filter.filters;

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
 * @version 1.0
 * @date 2016/3/2
 */
public class ContractFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
       /* if(logger.isInfoEnabled()){*/
            logger.info("do filter process...");
        /*}*/
        Rule rule1 = request.getRuleMap().get("1020"); // 合约订单类型规则
        Rule rule2 = request.getRuleMap().get("1021"); // 合约订单发货站点类型规则
        if (WaybillCacheHelper.isContract(request.getWaybillCache(), rule1.getContent())
                && !SiteHelper.matchSiteTypeRule(rule2.getContent(), request.getsReceiveSiteSubType())) {
            throw new SortingCheckException(SortingResponse.CODE_29105,
                    SortingResponse.MESSAGE_29105);
        }

        chain.doFilter(request, chain);
    }
}
