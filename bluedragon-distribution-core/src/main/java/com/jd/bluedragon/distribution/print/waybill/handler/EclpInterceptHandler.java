package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.BusinessHelper;

/**
 * 
 * @ClassName: EclpInterceptHandler
 * @Description: eclp打印业务拦截
 * @author: wuyoude
 * @date: 2019年3月20日 下午6:51:57
 *
 */
@Service("eclpInterceptHandler")
public class EclpInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>> {
    private static final Log logger = LogFactory.getLog(EclpInterceptHandler.class);

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        logger.info("eclpInterceptHandler-eclp打印业务拦截");
        InterceptResult<String> interceptResult = context.getResult();
        if(!BusinessHelper.isEclpCanPrint(context.getWaybill().getWaybillSign())){
        	interceptResult.toFail("此类运单不允许eclp系统操作打印！");
        }
        return interceptResult;
    }
}
