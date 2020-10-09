package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SiteHelper;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/3
 */
public class AuctionFilter implements Filter {

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Boolean isStoreHouse = SiteHelper.isStoreHouse(request.getReceiveSite());
        if (isStoreHouse) {
            if (BusinessHelper.isAuction(request.getWaybillCache().getWaybillType())) {
                //夺宝岛（waybillType=2）订单不能发往大库（类型=900）
                throw new SortingCheckException(SortingResponse.CODE_29304, SortingResponse.MESSAGE_29304);
            }
        }

        chain.doFilter(request, chain);
    }
}
