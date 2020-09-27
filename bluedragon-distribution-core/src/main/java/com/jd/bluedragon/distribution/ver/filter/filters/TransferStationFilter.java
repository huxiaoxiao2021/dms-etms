package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.common.domain.WaybillCache;
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
        Site transferStationSite = request.getWaybillCache().getTransferStationId() != null ? siteService
                .get(request.getWaybillCache().getTransferStationId())
                : null;
        this.logger.info("waybill.getTransferStationId is " + request.getWaybillCache().getTransferStationId());

        //收获站点不是分拣中心
        if (this.hasTransferStation(request.getWaybillCache()) && !SiteHelper.isDistributionCenter(request.getReceiveSite())) {
            //站点相同
            boolean istransferStationSiteEquals = request.getReceiveSite().getCode().equals(request.getsReceiveBoxSite().getCode());
            //类型相同
            boolean istransferStationSiteReceiveSiteEquals = false;
            if(transferStationSite != null){
                istransferStationSiteReceiveSiteEquals = transferStationSite.getCode().equals(request.getsReceiveBoxSite().getCode());
            }
            //transferStation不是速递中心
            if (SiteHelper.isFastStation(transferStationSite)) {
                if (!istransferStationSiteEquals || !istransferStationSiteReceiveSiteEquals) {
                    throw new SortingCheckException(SortingResponse.CODE_39118,
                            SortingResponse.MESSAGE_39118);
                }
            } else {
                throw new SortingCheckException(SortingResponse.CODE_39003,
                        SortingResponse.MESSAGE_39003);
            }
        }
        //endregion

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
