package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.constants.HintModuleConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *弃件拦截
 * @author: biyubo
 * @date: 2021/03/15 11:27
 */
public class IsWasteWaybillFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WaybillTraceManager waybillTraceManager;


    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        logger.info("do IsWasteWaybillFilter process waybillCode[{}]" + request.getWaybillCode());
        String waybillCode = request.getWaybillCode();
        if (waybillTraceManager.isWaybillWaste(waybillCode)){
            throw new SortingCheckException(SortingResponse.CODE_29320, HintService.getHintWithFuncModule(HintCodeConstants.WASTE_WAYBILL_TEMP_STORE, request.getFuncModule()));
        }

        chain.doFilter(request, chain);
    }
}
