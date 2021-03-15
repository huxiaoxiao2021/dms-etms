package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.merchantWeightAndVolume.service.MerchantWeightAndVolumeWhiteListService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 是否为弃件判断
 *
 * @author: biyubo
 * @date: 2021/03/15 15:52
 */
@Service("isWasteWaybillHandler")
public class IsWasteWaybillHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger log = LoggerFactory.getLogger(IsWasteWaybillHandler.class);

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        try {
            String waybillCode = WaybillUtil.getWaybillCode(context.getRequest().getBarCode());
            if (waybillTraceManager.isWaybillWaste(waybillCode)){
                interceptResult.toFail(SortingResponse.CODE_29320,SortingResponse.MESSAGE_29320);
                log.info("IsWasteWaybillHandler-->弃件不允许换单。原单号:{}", waybillCode);
            }
        }catch (Exception e){
            log.error("运单弃件拦截异常!");
        }
        return interceptResult;
    }
}
