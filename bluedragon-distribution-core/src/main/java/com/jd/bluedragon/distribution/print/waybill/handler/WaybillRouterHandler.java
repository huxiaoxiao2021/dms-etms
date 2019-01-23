package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * 面单路由信息处理单元
 * 始发、目的
 * 各级路由节点
 */
@Service
public class WaybillRouterHandler implements Handler<WaybillPrintContext,JdResult<String>> {
    private static final Log logger= LogFactory.getLog(WaybillRouterHandler.class);

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        logger.info("获取路由信息");
        //1.获取始发和目的
        Integer dmsCode = context.getRequest().getDmsSiteCode();
        Integer targetSiteCode = context.getRequest().getTargetSiteCode();
        //2.根据始发和目的调路由接口，获取路由节点
        //3.循环路由节点，调基础资料接口，获取城市信息
        return context.getResult();
    }
}
