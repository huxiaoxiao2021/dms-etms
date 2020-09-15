package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * B网包装耗材确认拦截
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年08月27日 21时:29分
 */
public class WaybillConsumableFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {
        logger.info("B网包装耗材确认拦截开始...");
        Boolean isConfirmed = null;
        try {
            String waybillSign = request.getWaybillCache().getWaybillSign();
            if(BusinessUtil.isNeedConsumable(waybillSign)){
                isConfirmed =  this.isConsumableConfirmed(request.getWaybillCode());
                logger.info("B网包装耗材确认拦截开始,isConfirmed:" + isConfirmed);
            }
        }catch (Exception e){
            logger.error("查询运单是否已经确认耗材失败，运单号：" + JsonHelper.toJson(request), e);
        }

        if(Boolean.FALSE.equals(isConfirmed)){
            throw new SortingCheckException(SortingResponse.CODE_29120, SortingResponse.MESSAGE_29120);
        }

        chain.doFilter(request, chain);
    }


    private Boolean isConsumableConfirmed(String waybillCode) {
        try {
            return waybillConsumableRecordService.isConfirmed(waybillCode);
        } catch (Exception e) {
            logger.error("isConsumableConfirmed error:{}", waybillCode, e);
            return null;
        }

    }
}
