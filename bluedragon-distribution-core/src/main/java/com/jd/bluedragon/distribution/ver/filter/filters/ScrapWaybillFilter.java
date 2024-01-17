package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;

import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.SCRAP_WAYBILL_INTERCEPT_HINT_CODE;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isScrapWaybill;

/**
 * 无残值旧件报废拦截
 * https://joyspace.jd.com/pages/F9RjcFLeuMrgv4cfm4Nk
 */
public class ScrapWaybillFilter implements Filter {

    @Resource
    private WaybillService waybillService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        String packageCode = request.getPackageCode();
        if (!StringUtils.isEmpty(packageCode)) {
            BigWaybillDto waybill = waybillService.getWaybill(WaybillUtil.getWaybillCode(packageCode));
            if (waybill != null && waybill.getWaybill() != null && StringUtils.isNotEmpty(waybill.getWaybill().getWaybillSign())) {
                // waybillSign的19位等于2是报废运单 拦截
                String waybillSign = waybill.getWaybill().getWaybillSign();
                if (isScrapWaybill(waybillSign)) {
                    throw new SortingCheckException(Integer.valueOf(SCRAP_WAYBILL_INTERCEPT_HINT_CODE),
                            HintService.getHint(SCRAP_WAYBILL_INTERCEPT_HINT_CODE));
                }
            }
        }
        chain.doFilter(request,chain);
    }
}
