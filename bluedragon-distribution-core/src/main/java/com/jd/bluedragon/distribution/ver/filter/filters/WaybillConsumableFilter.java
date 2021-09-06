package com.jd.bluedragon.distribution.ver.filter.filters;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
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
        Boolean isNotFound = null;
        try {
            String waybillSign = request.getWaybillCache().getWaybillSign();
            //只有B网进行校验
            if(BusinessUtil.isNeedConsumable(waybillSign)
              && Integer.valueOf(Constants.B2B_SITE_TYPE).equals(request.getCreateSite().getSubType())){
                isNotFound = isNotFoundRecord(request.getWaybillCode());
                isConfirmed =  this.isConsumableConfirmed(request.getWaybillCode());
                logger.info("B网包装耗材确认拦截开始"+request.getWaybillCode()+",isConfirmed:" + isConfirmed + " isNotFound:"+isNotFound);
            }
        }catch (Exception e){
            logger.error("查询运单是否已经确认耗材失败，运单号：" + JsonHelper.toJson(request), e);
        }
        if(isConfirmed != null && isNotFound != null){
            if(Boolean.FALSE.equals(isNotFound) && Boolean.FALSE.equals(isConfirmed)){
                throw new SortingCheckException(SortingResponse.CODE_29120,
                        HintService.getHintWithFuncModule(HintCodeConstants.PACKING_CONSUMABLE_CONFIRM, request.getFuncModule()));
            }
            if(Boolean.TRUE.equals(isNotFound)){
                //此运单需使用包装耗材，但不存在包装耗材任务  提示 不拦截
                throw new SortingCheckException(SortingResponse.CODE_39119,
                        HintService.getHintWithFuncModule(HintCodeConstants.PACKING_CONSUMABLE_NOT_EXIST, request.getFuncModule()));
            }
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

    private Boolean isNotFoundRecord(String waybillCode) {
        try {
            return waybillConsumableRecordService.queryOneByWaybillCode(waybillCode) == null;
        } catch (Exception e) {
            logger.error("isNotFoundRecord error:{}", waybillCode, e);
            return null;
        }

    }
}
