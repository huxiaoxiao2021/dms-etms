package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.springframework.stereotype.Service;

/**
 * 经济网处理器
 *      经济网不显示任何京东标识
 *      经济网不支持10x5面单，只使用10x10面单（dms-unite1010-m）
 * @author: hujiping
 * @date: 2020/1/13 14:42
 */
@Service("businessNetInterceptHandler")
public class BusinessNetInterceptHandler implements InterceptHandler<WaybillPrintContext,String> {

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        WaybillPrintRequest request = context.getRequest();
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        Waybill waybill = context.getWaybill();
        String waybillSign = waybill==null?null:waybill.getWaybillSign();
        if(BusinessUtil.isBusinessNet(waybillSign)){
            basePrintWaybill.setJdLogoImageKey("");
            basePrintWaybill.setPopularizeMatrixCode("");
            basePrintWaybill.setAdditionalComment("");
            basePrintWaybill.setComment("");
        }
        return result;
    }
}
