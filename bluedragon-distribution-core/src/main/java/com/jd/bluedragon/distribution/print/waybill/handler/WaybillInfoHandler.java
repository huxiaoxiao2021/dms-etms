package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 调用运单接口，获取运单信息
 * Created by xumei3 on 2018/8/13.
 */
public class WaybillInfoHandler implements InterceptHandler<WaybillPrintContext,String> {
    private static final Log logger = LogFactory.getLog(BasicWaybillPrintHandler.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();
        String waybillCode = BusinessHelper.getWaybillCode(context.getRequest().getBarCode());
        try {
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(waybillCode);
            if(baseEntity != null && Constants.RESULT_SUCCESS == baseEntity.getResultCode()){
                //运单数据为空，直接返回运单数据为空异常
                if(baseEntity.getData() == null
                        ||baseEntity.getData().getWaybill() == null){
                    interceptResult.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(), WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
                    logger.warn("调用运单接口获取运单数据为空，waybillCode："+waybillCode);
                    return interceptResult;
                }
                //获取运单数据正常，设置打印基础信息
                context.setBigWaybillDto(baseEntity.getData());
            }else if(baseEntity != null && Constants.RESULT_SUCCESS != baseEntity.getResultCode()){
                interceptResult.toError(InterceptResult.CODE_ERROR, baseEntity.getMessage());
            }else{
                interceptResult.toError(InterceptResult.CODE_ERROR, "运单数据为空！");
            }
        }catch (Exception ex){
            logger.error("标签打印接口异常",ex);
            interceptResult.toError();
        }
        return interceptResult;
    }
}
