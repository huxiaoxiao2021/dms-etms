package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
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
        WaybillPrintResponse response = context.getResponse();
        if(BusinessUtil.isBusinessNet(response.getWaybillSign())){
            if(DmsPaperSize.PAPER_SIZE_CODE_1005.equals(request.getPaperSizeCode())){
                //经济网不支持10x5面单
                result.toFail("经济网单号不支持10x5模板，请选择10x10模板!");
                return result;
            }
            response.setJdLogoImageKey("");
            response.setPopularizeMatrixCode("");
            response.setAdditionalComment("");
            response.setComment("");
            response.setRemark(response.getBusiOrderCode());
        }
        return result;
    }
}
