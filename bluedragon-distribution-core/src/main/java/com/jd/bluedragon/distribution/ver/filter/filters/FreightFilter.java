package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * B网运单运费（到付，寄付）拦截校验
 *
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年12月19日 18时:08分
 */
public class FreightFilter implements Filter {

    private static final Log logger = LogFactory.getLog(FreightFilter.class);

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        WaybillCache waybillCache = request.getWaybillCache();

        //b2b校验是否包含-到付运费
        if (BusinessHelper.isCheckFreightForB2b(waybillCache.getWaybillSign())) {
            fixFreight(waybillCache);
            if (!NumberHelper.gt0(waybillCache.getFreight())) {
                logger.warn("运单无到付运费金额:" + waybillCache.getWaybillCode());
                throw new SortingCheckException(SortingResponse.CODE_29405, SortingResponse.MESSAGE_29405);
            }
        }
        //b2b校验是否包含-寄付运费
        if (BusinessHelper.isCheckSendFreightForB2b(waybillCache.getWaybillSign())) {
            fixFreight(waybillCache);
            if (!NumberHelper.gt0(waybillCache.getFreight())) {
                logger.warn("运单无寄付运费金额:" + waybillCache.getWaybillCode());
                throw new SortingCheckException(SortingResponse.CODE_29406, SortingResponse.MESSAGE_29406);
            }
        }

        chain.doFilter(request, chain);
    }

    /**
     * 运单没有运费字段时，请求运单获取最新的数据
     */
    private void fixFreight(WaybillCache waybillCache) throws SortingCheckException{
        if (StringUtils.isEmpty(waybillCache.getFreight())) {
            WaybillCache waybillNoCache = null;
            try {
                waybillNoCache = waybillCacheService.getNoCache(waybillCache.getWaybillCode());
            } catch (Exception e) {
                logger.error("运费拦截查询运单信息失败： " + waybillCache.getWaybillCode(), e);
            }
            if (waybillNoCache == null) {
                throw new SortingCheckException(SortingResponse.CODE_39002, SortingResponse.MESSAGE_39002);
            }
            waybillCache.setFreight(waybillNoCache.getFreight());
        }
    }
}
