package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import org.apache.commons.lang.StringUtils;

import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.SCRAP_WAYBILL_INTERCEPT_HINT_CODE;
import static com.jd.bluedragon.distribution.api.response.SortingResponse.SCRAP_WAYBILL_INTERCEPT_CODE;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isScrapWaybill;

/**
 * 无残值旧件报废拦截
 * https://joyspace.jd.com/pages/F9RjcFLeuMrgv4cfm4Nk
 */
public class ScrapWaybillFilter implements Filter {

    @Override
    public void doFilter(FilterContext context, FilterChain chain) throws Exception {
        String packageCode = context.getPackageCode();
        if (!StringUtils.isEmpty(packageCode)) {
            String waybillSign = context.getWaybillCache().getWaybillSign();
            if (StringUtils.isNotEmpty(waybillSign)) {
                // waybillSign的19位等于2是报废运单 拦截
                if (isScrapWaybill(waybillSign)) {
                    throw new SortingCheckException(SCRAP_WAYBILL_INTERCEPT_CODE,
                            HintService.getHint(SCRAP_WAYBILL_INTERCEPT_HINT_CODE));
                }
            }
        }
        chain.doFilter(context,chain);
    }
}
