package com.jd.bluedragon.core.message.base;


public abstract class MessageBaseAbstractFactory {
    
    public abstract MessageBaseConsumer createMessageConsumer(String destination, String consumer);
    
}
