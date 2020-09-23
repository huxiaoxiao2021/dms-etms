package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.BusinessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/3
 */
public class RWaybillFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SiteService siteService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //大件订单 不进行预分拣相关校验 tangcq2018年11月2日10:28:42
        if (BusinessHelper.isLasOrder(request.getWaybillCache().getWaybillSign())){
            chain.doFilter(request, chain);
            return;
        }
        // 判断预分拣站点是否合法,
        // 如果预分拣站点不合法，则使用反调度站点代替
        Site waybillSite = null;
        if (request.getWaybillCache().getSiteCode() != null && request.getWaybillCache().getSiteCode() > 0) {
            waybillSite = siteService.get(request.getWaybillCache().getSiteCode());
        }

        if (null == waybillSite || null == waybillSite.getCode() || waybillSite.getCode() <= 0) {// 如果预分拣站点没有,则查找反调度站点(逆向也有反调度)
            Integer sceduleSiteCode = siteService.getLastScheduleSite(request.getWaybillCache().getWaybillCode());
            if (sceduleSiteCode != null) {
                waybillSite = siteService.get(sceduleSiteCode);
            } else {
                throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SITE_NULL, SortingResponse.MESSAGE_WAYBILL_SITE_NULL);
            }
        }

        // 获取正常站点\反调度站点后，如果预分拣站点仍然不存在或者<0，则提示超区订单
        if (null == waybillSite || null == waybillSite.getCode() || waybillSite.getCode() < 0) {
            throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SUPER_AREA, SortingResponse.MESSAGE_WAYBILL_SUPER_AREA);
        }

        /**
         * 逆向 预分拣目的地强制校验
         */
        if(BusinessHelper.isMustPerSortingSite(request.getWaybillCache().getWaybillSign()) && BusinessHelper.isWMSBySiteType(request.getReceiveSite().getType())){
            if(!waybillSite.getCode().equals(request.getReceiveSiteCode())){
                throw new SortingCheckException(SortingResponse.CODE_IS_MUST_PER_SORITNG_SITE, SortingResponse.MESSAGE_IS_MUST_PER_SORITNG_SITE);
            }
        }
        request.setWaybillSite(waybillSite);
        chain.doFilter(request, chain);
    }
}
