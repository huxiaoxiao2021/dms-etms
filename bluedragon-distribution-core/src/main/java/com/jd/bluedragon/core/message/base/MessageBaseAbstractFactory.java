package com.jd.bluedragon.core.message.base;

@Deprecated
public abstract class MessageBaseAbstractFactory {
    
    public abstract MessageBaseConsumer createMessageConsumer(String destination, String consumer);
    
}
