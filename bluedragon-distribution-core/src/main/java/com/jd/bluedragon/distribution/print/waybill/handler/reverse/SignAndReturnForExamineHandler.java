package com.jd.bluedragon.distribution.print.waybill.handler.reverse;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.receive.service.SignBillReturnApiManager;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: liming522
 * @Description: 签单返还 审批校验
 * @Date: create in 2021/4/25 16:26
 */
@Service("signAndReturnForExamineHandler")
public class SignAndReturnForExamineHandler implements InterceptHandler<WaybillPrintContext,String> {

    private static final Logger logger = LoggerFactory.getLogger(SignAndReturnForExamineHandler.class);

    @Autowired
    private SignBillReturnApiManager signBillReturnApiManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        logger.debug("签单返还拦截校验");
        InterceptResult<String> result = context.getResult();

        // 只有包裹补打和换单打印走以下逻辑
        Integer operateType = context.getRequest().getOperateType();
        if(!WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType().equals(operateType)&&!WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(operateType)){
            return result;
        }

        String newWaybillCode = context.getBigWaybillDto().getWaybill().getWaybillCode();
        Integer siteCode = context.getRequest().getSiteCode();

        try {
            String waybillSign = context.getBigWaybillDto().getWaybill().getWaybillSign();

            //包裹补打、换单打印-传递的就是新单号
            if(!BusinessUtil.isSignBack(waybillSign)){
                return result;
            }

            InvokeResult<Boolean> invokeResult =  signBillReturnApiManager.checkSignBillReturn(newWaybillCode,siteCode);
            if(!invokeResult.codeSuccess()){
                result.toFail(invokeResult.getMessage());
                return result;
            }
        }catch (Exception e){
            logger.error("判断签单返还审批状态接口异常 waybillCode:{},siteCode:{}",newWaybillCode,siteCode,e);
            result.toError(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
    
