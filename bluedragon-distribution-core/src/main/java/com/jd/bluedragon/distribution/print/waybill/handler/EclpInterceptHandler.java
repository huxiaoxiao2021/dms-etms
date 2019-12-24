package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.BusinessHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private static final Logger log = LoggerFactory.getLogger(EclpInterceptHandler.class);

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        log.debug("eclpInterceptHandler-eclp打印业务拦截");
        InterceptResult<String> interceptResult = context.getResult();
        if(!BusinessHelper.isEclpCanPrint(context.getWaybill().getWaybillSign())){
        	interceptResult.toFail("此类运单不允许eclp系统操作打印！");
        }
        return interceptResult;
    }
}
