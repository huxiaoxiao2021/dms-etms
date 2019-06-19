package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 
 * @ClassName: EclpShoppingInterceptHandler
 * @Description: eclp商城打印业务拦截
 * @author: wuyoude
 * @date: 2019年3月20日 下午6:51:57
 *
 */
@Service("eclpShoppingInterceptHandler")
public class EclpShoppingInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>> {
    private static final Log logger = LogFactory.getLog(EclpShoppingInterceptHandler.class);

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        logger.info("eclpShoppingInterceptHandler-eclp商城打印业务拦截");
        InterceptResult<String> interceptResult = context.getResult();
        //请求的siteName传递BW码作为商家唯一编码
        String businessCode = context.getRequest().getSiteName();
        String waybillBusinessCode = context.getBigWaybillDto().getWaybill().getMemberId();
        //商家BW码为空或者与运单不一致禁止打印
        if(StringHelper.isEmpty(businessCode)){
        	interceptResult.toFail("传入参数中select* from商家编码标识不能为空！");
        } else if(!businessCode.equals(waybillBusinessCode)){
        	interceptResult.toFail("商家编码标识与运单不一致，禁止打印！");
        }
        return interceptResult;
    }
}
