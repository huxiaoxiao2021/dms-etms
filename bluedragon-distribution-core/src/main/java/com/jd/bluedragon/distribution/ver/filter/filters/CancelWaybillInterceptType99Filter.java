package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fanggang7
 * @time 2023-02-03 18:33:28 周五
 */
public class CancelWaybillInterceptType99Filter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        try {
            if (WaybillUtil.isPackageCode(request.getPackageCode())) {
                final boolean hasIntercept = waybillCancelService.checkWaybillCancelInterceptType99(WaybillUtil.getWaybillCode(request.getPackageCode()));
                if(hasIntercept){
                    throw new SortingCheckException(SortingResponse.CODE_29325, HintService.getHint(HintCodeConstants.CUSTOM_INTERCEPT));
                }
            }
        } catch (SortingCheckException e) {
            throw e;
        } catch (Exception e){
            logger.error("CancelWaybillInterceptType99Filter exception ", e);
        }

        chain.doFilter(request, chain);
    }

}
