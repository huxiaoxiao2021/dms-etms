package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SiteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/3/1
 */
public class HeZuoDaiShouFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Integer DISTRIBUTE_CENTER_TYPE = 64;

    @Autowired
    private BaseService baseService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Boolean isSelfOrderDisToSelfOrderSiteDaiShou = Boolean.FALSE;
        if (BusinessUtil.isHeZuoDaiShou(request.getWaybillCache().getSendPay())) {
            if (SiteHelper.isPickup(request.getReceiveSite())) {
                throw new SortingCheckException(SortingResponse.CODE_29118,
                        SortingResponse.MESSAGE_29118);
            }
            // 自提柜跨分拣取消提示
            if (!SiteHelper.matchSiteRule(SortingResponse.CODE_SiteType_DaiShou, request.getsReceiveSiteSubType()) && !DISTRIBUTE_CENTER_TYPE.equals(request.getReceiveSite().getType())) {
                // 从基础资料的站点-自提柜绑定关系中找出自提柜所属站点
                if (null == request.getWaybillSite() || null == request.getWaybillSite().getCode() || request.getWaybillSite().getCode() <= 0) {
                    throw new SortingCheckException(SortingResponse.CODE_WAYBILL_SITE_NULL, SortingResponse.MESSAGE_WAYBILL_SITE_NULL);
                }

                Integer selfhelpBoxBelongSiteCode = baseService.getSiteSelfDBySiteCode(request.getWaybillSite().getCode());
                if (!SiteHelper.isMatchOfBoxBelongSiteAndReceivedSite(selfhelpBoxBelongSiteCode, request.getsReceiveSiteCode())) {
                    throw new SortingCheckException(SortingResponse.CODE_39130, SortingResponse.MESSAGE_39130);
                } else {
                    isSelfOrderDisToSelfOrderSiteDaiShou = Boolean.TRUE;
                }
            }

        } else if ( ! BusinessUtil.isHeZuoDaiShou(request.getWaybillCache().getSendPay())
                && SiteHelper.matchSiteRule(SortingResponse.CODE_SiteType_DaiShou, request.getsReceiveSiteSubType())) {
            throw new SortingCheckException(SortingResponse.CODE_29211,
                    SortingResponse.MESSAGE_29211);
        }

        request.setSelfOrderDisToSelfOrderSiteDaiShou(isSelfOrderDisToSelfOrderSiteDaiShou);

        chain.doFilter(request, chain);
    }
}
