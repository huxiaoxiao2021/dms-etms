package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.WaybillExtVO;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SiteHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * 港澳单审单拦截
 *  1、港澳单 && 非同城 报关审核失败拦截
 *  2、港澳单 && 非同城 && 中转站 报关审核中拦截
 *
 * @author hujiping
 * @date 2023/7/31 2:08 PM
 */
@Slf4j
public class InternationalExamineFilter implements Filter {
    
    @Autowired
    private WaybillService waybillService;
    
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        WaybillExtVO waybillExtVO = request.getWaybillCache().getWaybillExtVO();
        if(waybillExtVO != null 
                &&  BusinessUtil.isGAWaybill(waybillExtVO.getStartFlowDirection(), waybillExtVO.getEndFlowDirection())
                && !Objects.equals(waybillExtVO.getStartFlowDirection(), waybillExtVO.getEndFlowDirection())){
            
            CancelWaybill cancelWaybill = waybillService.queryGAExamineCancelWaybill(request.getWaybillCode());
            if(cancelWaybill == null){
                chain.doFilter(request, chain);
                return;
            }
            if(Objects.equals(cancelWaybill.getFeatureType(), CancelWaybill.FEATURE_TYPE_INTERCEPT_GA_EXAMINE_FAIL)){
                // 港澳单报关审核失败拦截
                throw new SortingCheckException(Integer.valueOf(HintCodeConstants.EXCEPTION_GA_EXAMINE_FAIL_INTERCEPT_TYPE),
                        HintService.getHint(HintCodeConstants.EXCEPTION_GA_EXAMINE_FAIL_INTERCEPT_TYPE));
            }
            if(SiteHelper.isSortTransferSite(request.getCreateSite())){
                if(Objects.equals(cancelWaybill.getFeatureType(), CancelWaybill.FEATURE_TYPE_INTERCEPT_GA_EXAMINE)){
                    // 港澳单报关审核中拦截
                    throw new SortingCheckException(Integer.valueOf(HintCodeConstants.EXCEPTION_GA_EXAMINE_INTERCEPT_TYPE),
                            HintService.getHint(HintCodeConstants.EXCEPTION_GA_EXAMINE_INTERCEPT_TYPE));
                }
            }
        }
        chain.doFilter(request, chain);
    }
}
