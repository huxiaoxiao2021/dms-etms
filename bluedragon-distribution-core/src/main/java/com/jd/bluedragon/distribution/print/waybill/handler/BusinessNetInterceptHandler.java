package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.stereotype.Service;

/**
 * 经济网处理器
 *      经济网不显示任何京东标识
 *      经济网不支持10x5面单，只使用10x10面单（dms-unite1010-m）
 *      (二期：去除10*5面单限制、去除remark备注)
 * @author: hujiping
 * @date: 2020/1/13 14:42
 */
@Service("businessNetInterceptHandler")
public class BusinessNetInterceptHandler extends AbstractInterceptHandler<WaybillPrintContext,String> {

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.BusinessNetInterceptHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        BasePrintWaybill basePrintWaybill = context.getBasePrintWaybill();
        Waybill waybill = context.getWaybill();
        String waybillSign = waybill==null?null:waybill.getWaybillSign();
        if(BusinessUtil.isBusinessNet(waybillSign)){
            basePrintWaybill.setJdLogoImageKey("");
            basePrintWaybill.setPopularizeMatrixCode("");
            basePrintWaybill.setAdditionalComment("");
            basePrintWaybill.setComment("");
            basePrintWaybill.setTransportMode("");
        }
        return result;
    }
}
