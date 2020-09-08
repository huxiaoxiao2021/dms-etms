package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 7fresh见标拦截过滤器
 */
public class SevenFreshFilter implements Filter {

    private static final Log logger = LogFactory.getLog(SevenFreshFilter.class);

    //判断7fresh拦截订单
    private static final Integer SEVEN_FRESH_INTERCEPT = 10001;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("7fresh见标拦截开始...");
        Integer waybillType = request.getWaybillCache().getType();
        if(SEVEN_FRESH_INTERCEPT.equals(waybillType)) {
            logger.info("7fresh见标拦截,waybillType:" + waybillType);
            throw new SortingCheckException(SortingResponse.CODE_29119, SortingResponse.MESSAGE_29119);
        }

        chain.doFilter(request, chain);
    }
}
