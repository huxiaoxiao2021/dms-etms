package com.jd.bluedragon.common;

import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jsf.gd.filter.AbstractFilter;
import com.jd.jsf.gd.msg.RequestMessage;
import com.jd.jsf.gd.msg.ResponseMessage;
import com.jd.jsf.gd.util.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class ProviderLogFilter extends AbstractFilter {
    private static Logger LOGGER = LoggerFactory.getLogger(ProviderLogFilter.class);

    @Override
    public ResponseMessage invoke(RequestMessage requestMessage) {
        Object traceId = RpcContext.getContext().getAttachment("traceId");
        if (!ObjectHelper.isNotNull(traceId)){
            traceId = UUID.randomUUID().toString().replace("-","");
        }
        LOGGER.info("========jsfProviderLogFilter======== traceId:{}", traceId);
        MDC.put("traceId",String.valueOf(traceId));
        return this.getNext().invoke(requestMessage);
    }
}
