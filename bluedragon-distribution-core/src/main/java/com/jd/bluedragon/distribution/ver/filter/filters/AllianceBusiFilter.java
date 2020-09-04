package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 加盟商交接校验
 * 无交接动作提示现场交接
 */
public class AllianceBusiFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        //只有加盟商运单校验
        if(! this.check(request.getWaybillCache())) {
            throw new SortingCheckException(SortingResponse.CODE_29552, SortingResponse.MESSAGE_29552);
        }
        chain.doFilter(request,chain);
    }

    private boolean check(WaybillCache waybillCache) {
        //校验是否为加盟商运单
        if(BusinessUtil.isForeignForward(waybillCache.getWaybillSign()) && BusinessUtil.isAllianceBusi(waybillCache.getWaybillSign())){
            try {
                if (! allianceBusiDeliveryDetailService.checkExist(waybillCache.getWaybillCode())) {
                    //未入池 提示操作称重交接
                    return false;
                }
            } catch (Exception e){
                logger.error("校验加盟商运单异常"+waybillCache.getWaybillCode(), e);
            }
        }
        return true;
    }
}
