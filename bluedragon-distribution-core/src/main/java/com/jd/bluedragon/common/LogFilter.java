package com.jd.bluedragon.common;

import com.jd.jsf.gd.filter.AbstractFilter;
import com.jd.jsf.gd.msg.RequestMessage;
import com.jd.jsf.gd.msg.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFilter extends AbstractFilter {
    private static Logger LOGGER = LoggerFactory.getLogger(LogFilter.class);
    @Override
    public ResponseMessage invoke(RequestMessage requestMessage) {
        LOGGER.info("========LogFilter======");

        return this.getNext().invoke(requestMessage);
    }
}
