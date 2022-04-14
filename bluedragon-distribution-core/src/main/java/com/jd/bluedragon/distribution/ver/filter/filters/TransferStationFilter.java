package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.SiteHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class TransferStationFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SiteService siteService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //region 中转站订单判断

        // 2021年12月15日17:56:08 下线

        chain.doFilter(request, chain);
    }

    private Boolean hasTransferStation(WaybillCache waybill) {
        if (waybill == null || waybill.getTransferStationId() == null) {
            return Boolean.FALSE;
        }

        if (waybill.getTransferStationId() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
