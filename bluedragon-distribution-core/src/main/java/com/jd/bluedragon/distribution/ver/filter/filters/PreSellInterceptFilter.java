package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsMessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 预售发货拦截处理
 * @author wuyoude
 *
 */
public class PreSellInterceptFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        String sendPay = null;
        if (request.getWaybillCache() != null) {
        	sendPay = request.getWaybillCache().getSendPay();
        }
        //预售未付款，提示退仓
        if(BusinessUtil.isPreSellWithNoPay(sendPay)) {
            throw new SortingCheckException(DmsMessageConstants.CODE_29419, DmsMessageConstants.MESSAGE_29419);
        }
        chain.doFilter(request, chain);
    }
}
