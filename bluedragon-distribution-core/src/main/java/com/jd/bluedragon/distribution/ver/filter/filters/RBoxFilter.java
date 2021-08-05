package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
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
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_BEGINNING_DIFFERENT_FROM_CURRENT_SITE, request.getFuncModule()));
            }

        	/*added by zhanglei 20160623 已发货的箱子不允许继续装箱 */
            if (this.statusValidation(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29011,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_HAS_SENT_GOODS, request.getFuncModule()));
            }

            if (BoxHelper.isLuxuryForForward(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29007,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_FORWARD_LUXURY_WAYBILL, request.getFuncModule()));
            } else if (BoxHelper.isOrdinaryForWarehouse(request.getBox())
                    && !SiteHelper.isWarehouse(rule2.getContent(), request.getsReceiveSiteSubType())
                    && !BusinessUtil.isBizSite(sReceiveSiteType)) {
                throw new SortingCheckException(SortingResponse.CODE_29004,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_COMMON_REVERSE, request.getFuncModule()));
            } else if (BoxHelper.isLuxuryForWarehouse(request.getBox())
                    && !SiteHelper.isWarehouse(rule2.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29008,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_REVERSE, request.getFuncModule()));
            } else if (BoxHelper.isOrdinaryForAfterSale(request.getBox())
                    && !SiteHelper.isAfterSale(rule3.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29005,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_COMMON_AFTER_SALE, request.getFuncModule()));
            } else if (BoxHelper.isLuxuryForAfterSale(request.getBox())
                    && !SiteHelper.isAfterSale(rule3.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29009,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_AFTER_SALE, request.getFuncModule()));
            }

            if ( WaybillUtil.isSurfaceCode(request.getPackageCode())) {
                if (WaybillCacheHelper.isLuxury(request.getWaybillCache()) && ! BoxHelper.isLuxuryForAfterSale2(request.getBox())
                        && !BusinessHelper.isAuction(request.getWaybillCache().getType())) {
                    throw new SortingCheckException(SortingResponse.CODE_29111,
                            HintService.getHintWithFuncModule(HintCodeConstants.PICKUP_LUXURY_BOX_FOR_LUXURY_WAYBILL, request.getFuncModule()));
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isLuxuryForReverse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29009,
                            HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_AFTER_SALE, request.getFuncModule()));
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isLuxuryForWarehouse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29008,
                            HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_REVERSE, request.getFuncModule()));
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isOrdinaryForWarehouse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29004,
                            HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_COMMON_REVERSE, request.getFuncModule()));
                }
            } else {
                if (WaybillCacheHelper.isLuxury(request.getWaybillCache()) && !BoxHelper.isLuxuryForWarehouse2(request.getBox())
                        && !BusinessHelper.isAuction(request.getWaybillCache().getType())) {
                    throw new SortingCheckException(SortingResponse.CODE_29110,
                            HintService.getHintWithFuncModule(HintCodeConstants.REVERSE_LUXURY_BOX_FOR_LUXURY_WAYBILL, request.getFuncModule()));
                } else if (!WaybillCacheHelper.isLuxury(request.getWaybillCache())
                        && BoxHelper.isLuxuryForWarehouse(request.getBox())) {
                    throw new SortingCheckException(SortingResponse.CODE_29008,
                            HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_REVERSE, request.getFuncModule()));
                }
            }
        }

        if (!BusinessUtil.isBoxcode(request.getBoxCode())) {
            if ( WaybillUtil.isSurfaceCode(request.getPackageCode())
                    && !SiteHelper.isAfterSale(rule3.getContent(), request.getsReceiveSiteSubType())) {
                throw new SortingCheckException(SortingResponse.CODE_29112,
                        HintService.getHintWithFuncModule(HintCodeConstants.PICKUP_WAYBILL, request.getFuncModule()));
            } else if (! WaybillUtil.isSurfaceCode(request.getPackageCode())
                    && !SiteHelper.isAfterSale(rule2.getContent(), request.getsReceiveSiteSubType())
                    && !BusinessUtil.isBizSite(sReceiveSiteType)) {
                throw new SortingCheckException(SortingResponse.CODE_29113,
                        HintService.getHintWithFuncModule(HintCodeConstants.REVERSE_WAYBILL, request.getFuncModule()));
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
