package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class ChangeWaybillSignFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillService waybillService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //region 订单信息修改的，拦截重新打印包裹标签
        if (WaybillCacheHelper.isChangeWaybillSign(request.getWaybillCache())) {
            BlockResponse response = null;
            if (WaybillUtil.isPackageCode(request.getPackageCode())) {
                response = waybillService.checkPackageBlock(request.getPackageCode(), CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
            } else {
                response = waybillService.checkWaybillBlock(request.getWaybillCode(), CancelWaybill.FEATURE_TYPE_ORDER_MODIFY);
            }
            if (response == null || !BlockResponse.UNBLOCK.equals(response.getCode())) {
                throw  new SortingCheckException(SortingResponse.CODE_39123, SortingResponse.MESSAGE_39123);
            }
        }
        //endregion

        chain.doFilter(request, chain);
    }
}
