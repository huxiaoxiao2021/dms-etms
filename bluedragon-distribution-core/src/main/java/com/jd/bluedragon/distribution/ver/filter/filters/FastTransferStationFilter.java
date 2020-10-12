package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/2
 */
public class FastTransferStationFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SiteService siteService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //增加判断，如果是速递中心，分拣站点等于速递中心的，但与预分拣站点不一致的，这是正常情况，不提示错误
        Boolean isTransferStationId = request.getWaybillCache().getTransferStationId() != null
                && request.getReceiveSiteCode().equals(request.getWaybillCache().getTransferStationId()) ? true
                : false;

        // 站点可能为空，为空设为0
        if (! WaybillCacheHelper.isSubway(request.getWaybillCache()) && ! WaybillCacheHelper.isHi24(request.getWaybillCache())) {
            String waybillSiteCode = String.valueOf(NumberHelper.getIntegerValue(request.getWaybillCache()
                    .getSiteCode()));
            // 订单的站点和预分拣站点不一致。并且预分拣站点不是分拣中心也不是中转站，并且也不是自提柜分拣到与自提柜绑定的站点，提示错误
            if (!SiteHelper.isDistributionCenter(request.getReceiveSite())
                    && !isTransferStationId
                    && !request.getSelfOrderDisToSelfOrderSite()
                    && !request.getSelfOrderDisToSelfOrderSiteBianMin()
                    && !request.getSelfOrderDisToSelfOrderSiteDaiShou()
                    && !request.getPartnerOrderDisToSelfOrderSite()) {

                if (null != request.getWaybillCache().getSiteCode() && request.getWaybillCache().getSiteCode() > 0) {
                    if (!waybillSiteCode.equalsIgnoreCase(request.getsReceiveSiteCode())) {//如果分拣站点与预分拣站点不一致

                        //站点不一致，判断是否是返调度站点。获取返调度站点进行比对,如果有的话.
                        Integer sceduleSiteCode = siteService.getLastScheduleSite(request.getPackageCode());
                        if (sceduleSiteCode != null && sceduleSiteCode.toString().equals(request.getsReceiveSiteCode())) {
                            //如果站点相等,则什么也不做
                        } else {
                            throw new SortingCheckException(SortingResponse.CODE_39000, SortingResponse.MESSAGE_39000);
                        }
                    }
                } else {//如果预分拣站点为空

                    //站点不一致，判断是否是返调度站点。获取返调度站点进行比对,如果有的话.
                    Integer sceduleSiteCode = siteService.getLastScheduleSite(request.getPackageCode());
                    if (sceduleSiteCode != null && sceduleSiteCode.toString().equals(request.getsReceiveSiteCode())) {
                        //如果站点相等,则什么也不做
                    } else {
                        throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SITE_NULL, SortingResponse.MESSAGE_WAYBILL_SITE_NULL);
                    }

                }
            }
        }

        chain.doFilter(request, chain);
    }
}
