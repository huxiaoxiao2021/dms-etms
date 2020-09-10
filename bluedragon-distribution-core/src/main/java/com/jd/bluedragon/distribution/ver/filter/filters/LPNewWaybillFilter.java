package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 理赔换新单后 拦截新单
 *
 * @author 刘铎
 * @date 2018/07/24
 */
public class LPNewWaybillFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        if (request.getWaybillCache() != null && request.getWaybillCache().getWaybillSign() != null) {
            // 运单为理赔完成拦截 换新单时 拦截
            if(BusinessUtil.isLPNewWaybill(request.getWaybillCache().getWaybillSign())) {
                throw new SortingCheckException(SortingResponse.CODE_29309, SortingResponse.MESSAGE_29309);
            }
        }
        chain.doFilter(request, chain);
    }
}
