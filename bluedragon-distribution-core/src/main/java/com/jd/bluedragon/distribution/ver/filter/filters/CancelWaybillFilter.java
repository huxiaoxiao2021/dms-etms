package com.jd.bluedragon.distribution.ver.filter.filters;


import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dudong
 * @date 2016/2/29
 */
public class CancelWaybillFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final Integer siteType = 1024;

    @Autowired
    private SiteService siteService;

    @Autowired
    private WaybillService waybillService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        //region为了处理站点问题临时添加的校验s
        // 站点判断
        if (null != request.getReceiveSite() && null != request.getReceiveSite().getType() && request.getReceiveSite().getType().equals(siteType)) {
            request.setReceiveSite(this.siteService.getNoCache(request.getReceiveSiteCode()));
            if (request.getReceiveSite() == null) {
                throw new SortingCheckException(SortingResponse.CODE_29202, SortingResponse.MESSAGE_29202);
            } else {
                request.setsReceiveSiteSubType(String.valueOf(request.getReceiveSite().getSubType()));
                request.setsReceiveSiteCode(BusinessUtil.isBoxcode(request.getBoxCode())
                        ? String.valueOf(request.getBox().getReceiveSiteCode())
                        : String.valueOf(request.getReceiveSiteCode()));

                // 箱子的收货站点和站点类型 (中转站和速递中心判断使用)
                request.setsReceiveBoxSite(this.siteService.get(Integer.valueOf(request.getsReceiveSiteCode())));
            }
        }
        //endregion 为了处理站点问题临时添加的校验e

        //region 取消订单判断
        JdCancelWaybillResponse jdCancelWaybillResponse = request.getPdaOperateRequest() != null
                ? this.waybillService.dealCancelWaybill(request.getPdaOperateRequest())
                : this.waybillService.dealCancelWaybill(request.getWaybillCode());

        if (!jdCancelWaybillResponse.getCode().equals(JdResponse.CODE_OK)) {
            if(jdCancelWaybillResponse.getCode().equals(SortingResponse.CODE_29318)){
                if (null != request.getReceiveSite() && null != request.getReceiveSite().getType()) {
                    // 理赔拦截消息拦截，分站点提示消息
                    if(!BusinessUtil.isSortingSiteType(request.getReceiveSite().getType())){
                        jdCancelWaybillResponse.setMessage(SortingResponse.MESSAGE_29318_SITE);
                    }
                }
            }
            throw new SortingCheckException(jdCancelWaybillResponse.getCode(), jdCancelWaybillResponse.getMessage());
        }

        chain.doFilter(request, chain);
    }

}
