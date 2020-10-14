package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class LuxuryFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SiteService siteService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        logger.info("do filter process...");
        Rule rule1 = request.getRuleMap().get("1010"); // 奢侈品订单发货站点类型规则
        Rule rule2 = request.getRuleMap().get("1011"); // 奢侈品订单发货三方站点规则
        if (WaybillCacheHelper.isLuxury(request.getWaybillCache()) && ! BusinessHelper.isAuction(request.getWaybillCache().getWaybillType())) {
            if (SiteHelper.isPartnerBySiteSubType(request.getReceiveSite())
                    && !SiteHelper.matchSiteTypeRule(rule1.getContent(), request.getsReceiveSiteSubType())
                    && !SiteHelper.matchSiteRule(rule2.getContent(), request.getsReceiveSiteCode())) {
                throw new SortingCheckException(SortingResponse.CODE_29109,
                        SortingResponse.MESSAGE_29109);
            } else if (!SiteHelper.isPartnerBySiteSubType(request.getReceiveSite()) && !SiteHelper.matchSiteTypeRule(rule1.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29109,
                        SortingResponse.MESSAGE_29109);
            } else if (BoxHelper.isLuxuryForForward(request.getBox())) {
                Site site = siteService.get(request.getBox().getReceiveSiteCode());
                if ((site == null || site.getType() == null) && ! SiteHelper.isPartnerBySiteSubType(request.getReceiveSite())) {
                    String sBoxReceiveSiteType =  request.getsReceiveSiteSubType();
                    if (!SiteHelper.matchSiteTypeRule(rule1.getContent(), sBoxReceiveSiteType)) {
                        throw new SortingCheckException(SortingResponse.CODE_29109,
                                SortingResponse.MESSAGE_29109);
                    }
                }
            } else if (BusinessUtil.isBoxcode(request.getBoxCode()) && !BoxHelper.isLuxuryForForward2(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29107,
                        SortingResponse.MESSAGE_29107);
            }
        } else if (BoxHelper.isLuxuryForForward(request.getBox())) {
            throw new SortingCheckException(SortingResponse.CODE_29006,
                    SortingResponse.MESSAGE_29006);
        }

        chain.doFilter(request, chain);

        //endregion
    }
}
