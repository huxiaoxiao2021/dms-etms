package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BoxHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class FBoxFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoxService boxService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("do filter process...");
        if (BusinessUtil.isBoxcode(request.getBoxCode())) {

            //校验操作人分拣中心与箱号始发地是否一致
            if(!BoxHelper.isTheSameSiteWithOprator(request)){
                throw new SortingCheckException(SortingResponse.CODE_29010,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_BEGINNING_DIFFERENT_FROM_CURRENT_SITE, request.getFuncModule()));
            }

            if(this.statusValidation(request.getBox())){
                throw new SortingCheckException(SortingResponse.CODE_29011,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_HAS_SENT_GOODS, request.getFuncModule()));
            }

            if (BoxHelper.isOrdinaryForWarehouse(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29004,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_COMMON_REVERSE, request.getFuncModule()));
            } else if (BoxHelper.isLuxuryForWarehouse(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29008,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_REVERSE, request.getFuncModule()));
            } else if (BoxHelper.isOrdinaryForAfterSale(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29005,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_COMMON_AFTER_SALE, request.getFuncModule()));
            } else if (BoxHelper.isLuxuryForAfterSale(request.getBox())) {
                throw new SortingCheckException(SortingResponse.CODE_29009,
                        HintService.getHintWithFuncModule(HintCodeConstants.BOX_USE_FOR_LUXURY_AFTER_SALE, request.getFuncModule()));
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
