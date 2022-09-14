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

public class ConsumerLogFilter extends AbstractFilter {
    private static Logger LOGGER = LoggerFactory.getLogger(ConsumerLogFilter.class);

    @Override
    public ResponseMessage invoke(RequestMessage requestMessage) {
        String traceId =MDC.get("traceId");
        if (!ObjectHelper.isNotNull(traceId)){
            traceId =UUID.randomUUID().toString().replace("-","");
            MDC.put("traceId",traceId);
        }
        RpcContext.getContext().setAttachment("traceId",traceId);
        LOGGER.info("========jsfConsumerLogFilter======== traceId:{}", traceId);
        return this.getNext().invoke(requestMessage);
    }
}
