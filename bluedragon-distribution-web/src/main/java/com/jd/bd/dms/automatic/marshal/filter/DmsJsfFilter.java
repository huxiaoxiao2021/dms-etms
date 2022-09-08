package com.jd.bd.dms.automatic.marshal.filter;

import com.jd.bluedragon.core.context.InvokerClientInfoContext;
import com.jd.jsf.gd.config.annotation.AutoActive;
import com.jd.jsf.gd.config.annotation.Extensible;
import com.jd.jsf.gd.filter.AbstractFilter;
import com.jd.jsf.gd.msg.Invocation;
import com.jd.jsf.gd.msg.RequestMessage;
import com.jd.jsf.gd.msg.ResponseMessage;
import com.jd.jsf.gd.util.RpcContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bd.dms.automatic.marshal.filter
 * @ClassName: DmsJsfFilter
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/7 18:51
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Extensible(value = "clientInfo", order = 0)
@AutoActive(providerSide = true)
@Slf4j
public class DmsJsfFilter extends AbstractFilter {

    @Override
    public ResponseMessage invoke(RequestMessage requestMessage) {
        if (log.isDebugEnabled()) {
            log.debug("process DmsJsfFilter,class:{} method:{}", requestMessage.getClassName(), requestMessage.getMethodName());
        }
        Invocation invocation = requestMessage.getInvocationBody();
        ResponseMessage rm;

        try {
            InvokerClientInfoContext.ClientInfo clientInfo = new  InvokerClientInfoContext.ClientInfo();

//            InetSocketAddress address = RpcContext.getContext().getRemoteAddress();
            String ip = RpcContext.getContext().getRemoteHostName();
            clientInfo.setClientIp(ip);

            InvokerClientInfoContext.add(clientInfo);
        } catch (Exception ex) {
            log.error("process DmsJsfFilter class:{} method:{} 异常：{}", requestMessage.getClassName(), requestMessage.getMethodName(), ex.getMessage(), ex);
        }

        try {
            rm = this.getNext().invoke(requestMessage);
        } finally {
            try {
                if (log.isTraceEnabled()) {
                    log.trace("process DmsJsfFilter 清理数据");
                }
                //清理
                InvokerClientInfoContext.clear();
            } catch (Exception ex) {
                log.error("finally error:", ex);
            }

        }

        return rm;
    }
}
