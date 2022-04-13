package com.jd.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class SafetyShutDownConfig implements  ApplicationListener<ContextClosedEvent> {
    public static final Logger logger = LoggerFactory.getLogger(SafetyShutDownConfig.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        logger.info("ContextClosedEvent has been trigger ...");
    }
}
