package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 三方-合作站点的包裹可能要通过所绑定的自营分拣中心执行配送，
 * 这里做目的地不一致的拦截验证
 */
public class PartnerSiteFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Integer siteType = 1024;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Site waybillSite = request.getWaybillSite();


        //是否是三方-合作站点订单分拣到与三方-合作站点绑定的自营站点
        Boolean isPartnerOrderDisToSelfOrderSite = Boolean.FALSE;

        //预分拣站点是三方-合作站点，分拣目的地不是分拣中心时，包裹可能发送到其所属的自营站点，由自营站点配送到三方-合作站点
        if ((SiteHelper.isMayBelongSiteExist(waybillSite) || isSmallSite(waybillSite))
                && !SiteHelper.isDistributionCenter(request.getReceiveSite()) ) {

            if (null == waybillSite || null == waybillSite.getCode() || waybillSite.getCode() <= 0) {
                throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SITE_NULL, SortingResponse.MESSAGE_WAYBILL_SITE_NULL);
            }

            //预分拣站点和目的站点不符时，从基础资料的站点-合作站点绑定关系中找出三方-合作站点所属站点
            if(!SiteHelper.isMatchOfBoxBelongSiteAndReceivedSite(waybillSite.getCode(), request.getsReceiveSiteCode())){
                Integer belongSiteCode = baseMajorManager.getPartnerSiteBySiteId(waybillSite.getCode());
                if (!SiteHelper.isMatchOfBoxBelongSiteAndReceivedSite(belongSiteCode, request.getsReceiveSiteCode())) {
                    throw new SortingCheckException(SortingResponse.CODE_39000, SortingResponse.MESSAGE_39000);
                } else {
                    isPartnerOrderDisToSelfOrderSite = Boolean.TRUE;
                }
            }
        }

        request.setPartnerOrderDisToSelfOrderSite(isPartnerOrderDisToSelfOrderSite);

        chain.doFilter(request, chain);

    }

    /**
     * 是否是小站；true 是小站
     * @param preSite
     * @return
     */
    private boolean isSmallSite(Site preSite){
        if(preSite == null || preSite.getCode() == null || preSite.getCode() <= 0){
            return false;
        }
        Integer belongSiteCode = baseMajorManager.getPartnerSiteBySiteId(preSite.getCode());

        return belongSiteCode != null && belongSiteCode > 0;
    }
}
