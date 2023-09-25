package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 港澳D类运单集包拦截
 *  1、集包环节
 *  2、D类运单
 *
 * @author hujiping
 * @date 2023/7/31 2:08 PM
 */
@Slf4j
public class InternationalSortFilter implements Filter {
    
    // D类运单
    private static final String WAYBILL_TYPE_D = "D";
    
    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        
        if(BusinessUtil.isBoxcode(request.getBoxCode()) 
                && request.getWaybillCache().getWaybillExtVO() != null 
                && Objects.equals(request.getWaybillCache().getWaybillExtVO().getClearanceType(), WAYBILL_TYPE_D)){
            // 港澳D类运单禁止集包，只能按原包发货
            throw new SortingCheckException(Integer.valueOf(HintCodeConstants.EXCEPTION_GA_D_SORT_INTERCEPT_TYPE),
                    HintService.getHint(HintCodeConstants.EXCEPTION_GA_D_SORT_INTERCEPT_TYPE));
        }
        chain.doFilter(request, chain);
    }
}
