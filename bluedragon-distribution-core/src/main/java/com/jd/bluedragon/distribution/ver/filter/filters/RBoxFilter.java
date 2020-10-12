package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.rule.domain.Rule;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BoxHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.WaybillCacheHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/3
 */
public class RBoxFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoxService boxService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        Integer sReceiveSiteType = request.getReceiveSite().getType();
        Rule rule2 = request.getRuleMap().get("1080"); // 逆向库房类型规则
        Rule rule3 = request.getRuleMap().get("1090"); // 逆向售后类型规则
        if (BusinessUtil.isBoxcode(request.getBoxCode())) {

            /*added by zhanglei 20160621 校验操作人分拣中心与箱号始发地是否一致 */
            if(!BoxHelper.isTheSameSiteWithOprator(request)){
                throw new SortingCheckException(SortingResponse.CODE_29010,
                        SortingResponse.MESSAGE_29010);
            }

        	/*added by zhanglei 20160623 已发货的箱子不允许继续装箱 */
            if (this.statusValidation(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29011,
                        SortingResponse.MESSAGE_29011);
            }

            if (BoxHelper.isLuxuryForForward(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29007,
                        SortingResponse.MESSAGE_29007);
            } else if (BoxHelper.isOrdinaryForWarehouse(request.getBox())
                    && !SiteHelper.isWarehouse(rule2.getContent(), request.getsReceiveSiteSubType())
                    && !BusinessUtil.isBizSite(sReceiveSiteType)) {
                throw new SortingCheckException(SortingResponse.CODE_29004,
                        SortingResponse.MESSAGE_29004);
            } else if (BoxHelper.isLuxuryForWarehouse(request.getBox())
                    && !SiteHelper.isWarehouse(rule2.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29008,
                        SortingResponse.MESSAGE_29008);
            } else if (BoxHelper.isOrdinaryForAfterSale(request.getBox())
                    && !SiteHelper.isAfterSale(rule3.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29005,
                        SortingResponse.MESSAGE_29005);
            } else if (BoxHelper.isLuxuryForAfterSale(request.getBox())
                    && !SiteHelper.isAfterSale(rule3.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29009,
                        SortingResponse.MESSAGE_29009);
            }

            if ( WaybillUtil.isSurfaceCode(request.getPackageCode())) {
                if (WaybillCacheHelper.isLuxury(request.getWaybillCache()) && ! BoxHelper.isLuxuryForAfterSale2(request.getBox())
                        && !BusinessHelper.isAuction(request.getWaybillCache().getType())) {
                    throw new SortingCheckException(SortingResponse.CODE_29111,
                            SortingResponse.MESSAGE_29111);
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isLuxuryForReverse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29009,
                            SortingResponse.MESSAGE_29009);
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isLuxuryForWarehouse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29008,
                            SortingResponse.MESSAGE_29008);
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isOrdinaryForWarehouse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29004,
                            SortingResponse.MESSAGE_29004);
                }
            } else {
                if (WaybillCacheHelper.isLuxury(request.getWaybillCache()) && !BoxHelper.isLuxuryForWarehouse2(request.getBox())
                        && !BusinessHelper.isAuction(request.getWaybillCache().getType())) {
                    throw new SortingCheckException(SortingResponse.CODE_29110,
                            SortingResponse.MESSAGE_29110);
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isLuxuryForWarehouse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29008,
                            SortingResponse.MESSAGE_29008);
                }
            }
        }

        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            if ( WaybillUtil.isSurfaceCode(request.getPackageCode())
                    && !SiteHelper.isAfterSale(rule3.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29112,
                        SortingResponse.MESSAGE_29112);
            } else if (! WaybillUtil.isSurfaceCode(request.getPackageCode())
                    && !SiteHelper.isAfterSale(rule2.getContent(), request.getsReceiveSiteSubType())
                    && !BusinessUtil.isBizSite(sReceiveSiteType)) {
                throw new SortingCheckException(SortingResponse.CODE_29113,
                        SortingResponse.MESSAGE_29113);
            }
        }

        chain.doFilter(request, chain);
    }

    private boolean statusValidation(Box box) {
        if (box != null) {
            return boxService.checkBoxIsSent(box.getCode(), box.getCreateSiteCode());
        }
        return false;
    }
}
