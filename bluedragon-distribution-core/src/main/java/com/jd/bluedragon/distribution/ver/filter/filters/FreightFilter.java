package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;


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

        //b2b校验是否包含-到付运费 2021年08月30日13:47:07 移除

        //b2b校验是否包含-寄付运费 2021年12月15日17:51:51 移除

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
                throw new SortingCheckException(SortingResponse.CODE_39002, HintService.getHint(HintCodeConstants.WAYBILL_OR_PACKAGE_NOT_FOUND));
            }
            waybillCache.setFreight(waybillNoCache.getFreight());
        }
    }
}
