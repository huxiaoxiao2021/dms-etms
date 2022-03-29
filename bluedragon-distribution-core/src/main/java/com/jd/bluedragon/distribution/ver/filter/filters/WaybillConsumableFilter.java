package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.ver.domain.FilterContext;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.filter.Filter;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * B网包装耗材确认拦截
 * @author shipeilin
 * @Description: 类描述信息
 * 终端包装耗材重塑项目，快递快运都需要进行打木架，所以业务需求进行一次变更，凡事有包装耗材任务的单据都需要进行拦截
 * @date 2018年08月27日 21时:29分
 */
public class WaybillConsumableFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Override
    public void doFilter(FilterContext request, FilterChain chain) throws Exception {

        try {
            Boolean needConfirmed =  waybillConsumableRecordService.needConfirmed(request.getWaybillCode());
            logger.info("包装耗材确认拦截"+request.getWaybillCode()+",needConfirmed:" + needConfirmed );
            if (needConfirmed) {
                //强制拦截
                throw new SortingCheckException(SortingResponse.CODE_29120,
                        HintService.getHintWithFuncModule(HintCodeConstants.PACKING_CONSUMABLE_CONFIRM, request.getFuncModule()));
            }

        }catch (RuntimeException e){
            logger.error("查询运单是否已经确认耗材失败，运单号：" + JsonHelper.toJson(request), e);
        }

        chain.doFilter(request, chain);
    }
}
