package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class ChangeWaybillSignFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //region 订单信息修改的，拦截重新打印包裹标签
        if (WaybillCacheHelper.isChangeWaybillSign(request.getWaybillCache())) {
            throw  new SortingCheckException(SortingResponse.CODE_39123, SortingResponse.MESSAGE_39123);
        }
        //endregion

        chain.doFilter(request, chain);
    }
}
